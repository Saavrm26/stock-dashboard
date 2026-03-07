import yfinance as yf

class StocksService:
    def search(self, ticker: str) -> dict:
        stock = yf.Ticker(ticker)
        return stock.info
