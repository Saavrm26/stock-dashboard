from fastapi import APIRouter

from app.services.stocks_service import StocksService

router = APIRouter(prefix="/api/v1/stocks", tags=["stocks"])
stocks_service = StocksService()


@router.get("/search")
def search_stocks(query: str):
    return stocks_service.search(query)
