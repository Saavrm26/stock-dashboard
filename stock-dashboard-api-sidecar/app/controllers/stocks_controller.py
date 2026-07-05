from typing import List

from google.protobuf.json_format import MessageToDict
from fastapi import APIRouter

from app.services.stocks_service import StocksService

router = APIRouter(prefix="/api/v1/stocks", tags=["stocks"])
stocks_service = StocksService()


@router.get("/ticker-details")
def get_stock_details(query: str):
    details = stocks_service.get_ticker_details(query)
    return MessageToDict(details)


@router.get("/search")
def search_stocks(query: str):
    ticker_search = stocks_service.search_stock(query)
    return MessageToDict(ticker_search)


@router.post("/bluk/ticker-details/")
def bulk_get_stock_details(tickers: List[str]):
    details = stocks_service.bulk_get_ticker_details(tickers)
    return [MessageToDict(d) for d in details]