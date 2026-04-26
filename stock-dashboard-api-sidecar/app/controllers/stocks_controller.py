from google.protobuf.json_format import MessageToDict
from fastapi import APIRouter

from app.services.stocks_service import StocksService

router = APIRouter(prefix="/api/v1/stocks", tags=["stocks"])
stocks_service = StocksService()


@router.get("/ticker-details")
def search_stocks(query: str):
    details = stocks_service.get_ticker_details(query)
    return MessageToDict(details)
