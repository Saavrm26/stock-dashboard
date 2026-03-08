from google.protobuf.json_format import MessageToDict
from fastapi import APIRouter

from app.services.stocks_service import StocksService

router = APIRouter(prefix="/api/v1/stocks", tags=["stocks"])
stocks_service = StocksService()


@router.get("/search")
def search_stocks(query: str):
    details = stocks_service.search(query)
    return MessageToDict(details)
