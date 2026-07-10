from typing import List

import yfinance as yf

import asyncio
from app.db.redis import get_redis_client
from app.mappers.ticker_details_mapper import from_yfinance
from app.models.generated.ticker_details_pb2 import TickerDetails
from app.models.generated.ticker_search_pb2 import TickerSearchResponse
from google.protobuf.json_format import ParseDict


class StocksService:
    async def get_ticker_details(self, ticker: str) -> TickerDetails:
        r = get_redis_client()
        cache_data = await r.get(f"sidecar:ticker:{ticker}")
        if cache_data is not None and type(cache_data) == bytes:
            ticker_details = TickerDetails()
            ticker_details.ParseFromString(cache_data)
            return ticker_details
                
        stock = await asyncio.to_thread(yf.Ticker, ticker)
        info = stock.info
        ticker_details = ParseDict(info, TickerDetails(), ignore_unknown_fields=True)
        ticker_details_bytes = ticker_details.SerializeToString()
        await r.set(f"sidecar:ticker:{ticker}", ticker_details_bytes, ex=300)
        
        return ticker_details

    # How to cache this
    async def search_stock(self, query) -> TickerSearchResponse:
        search = await asyncio.to_thread(
            yf.Search, query, news_count=0, lists_count=0, enable_fuzzy_query=True
        )
        ticker_search = ParseDict(
            search.all, TickerSearchResponse(), ignore_unknown_fields=True
        )
        return ticker_search

    async def bulk_get_ticker_details(self, tickers: list[str]) -> list[TickerDetails]:
        if not tickers:
            return []
        
        r = get_redis_client()
        cache_keys = [f"sidecar:ticker:{t.upper()}" for t in tickers]
        
        # MGET all keys (unpack list as separate args)
        cached_values = await r.mget(*cache_keys)
        
        result : List[TickerDetails | None] = [None] * len(tickers)
        missing_indices = []
        missing_tickers = []
        
        # Process cache hits
        for i, (ticker, cached) in enumerate(zip(tickers, cached_values)):
            if cached is not None and isinstance(cached, bytes):
                td = TickerDetails()
                td.ParseFromString(cached)
                result[i] = td
            else:
                missing_indices.append(i)
                missing_tickers.append(ticker)
        
        # Fetch missing tickers from yfinance (deduplicate)
        if missing_tickers:
            # Deduplicate while preserving first occurrence index
            seen = {}
            unique_missing = []
            unique_indices = []
            for idx, ticker in zip(missing_indices, missing_tickers):
                ticker_upper = ticker.upper()
                if ticker_upper not in seen:
                    seen[ticker_upper] = len(unique_missing)
                    unique_missing.append(ticker)
                    unique_indices.append(idx)
            
            tickers_str = " ".join(t.upper() for t in unique_missing)
            multi = await asyncio.to_thread(yf.Tickers, tickers_str)
            
            # Prepare MSET mapping for new cache entries
            mset_mapping = {}
            
            for idx, ticker in zip(unique_indices, unique_missing):
                ticker_upper = ticker.upper()
                info = multi.tickers[ticker_upper].info
                td = ParseDict(info, TickerDetails(), ignore_unknown_fields=True)
                result[idx] = td
                # Prepare for batch cache write
                td_bytes = td.SerializeToString()
                mset_mapping[f"sidecar:ticker:{ticker_upper}"] = td_bytes
            
            # Batch write to cache with 5min TTL
            if mset_mapping:
                await r.mset(mset_mapping)
                # Set TTL on each key (mset doesn't support EX)
                for key in mset_mapping:
                    await r.expire(key, 300)
            
            # Fill duplicates from first occurrence
            for i, ticker in enumerate(missing_tickers):
                ticker_upper = ticker.upper()
                first_idx = seen[ticker_upper]
                result[missing_indices[i]] = result[unique_indices[first_idx]]
        
        return result
