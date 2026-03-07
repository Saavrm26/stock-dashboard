import uvicorn
from fastapi import FastAPI

from app.controllers.stocks_controller import router as stocks_router

app = FastAPI()
app.include_router(stocks_router)


def main():
    uvicorn.run(
        "main:app",
        host="127.0.0.1",
        port=8081,
        reload=True,
    )


if __name__ == "__main__":
    main()
