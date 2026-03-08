import yfinance as yf

from app.mappers.ticker_details_mapper import from_yfinance
from app.models.generated.ticker_details_pb2 import TickerDetails

class StocksService:
    def search(self, ticker: str) -> TickerDetails:
        stock = yf.Ticker(ticker)
        info = stock.info
        return from_yfinance(info)
