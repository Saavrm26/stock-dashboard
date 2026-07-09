import yfinance as yf

from app.mappers.ticker_details_mapper import from_yfinance
from app.models.generated.ticker_details_pb2 import TickerDetails
from app.models.generated.ticker_search_pb2 import TickerSearchResponse 
from google.protobuf.json_format import ParseDict

class StocksService:
    def get_ticker_details(self, ticker: str) -> TickerDetails:
        stock = yf.Ticker(ticker)
        info = stock.info
        return from_yfinance(info)
    
    def search_stock(self, query) -> TickerSearchResponse:
        search = yf.Search(query, news_count=0, lists_count=0, enable_fuzzy_query=True)
        ticker_search = ParseDict(search.all, TickerSearchResponse(), ignore_unknown_fields=True)
        return ticker_search

    def bulk_get_ticker_details(self, tickers: list[str]) -> list[TickerDetails]:
        tickers_str = " ".join(tickers)
        multi = yf.Tickers(tickers_str)
        return [from_yfinance(multi.tickers[t.upper()].info) for t in tickers]
