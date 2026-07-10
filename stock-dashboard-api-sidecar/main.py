from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI
import os
import redis.asyncio as redis

from app.controllers.stocks_controller import router as stocks_router
from app.db.redis import setup_redis

app_env = os.getenv("APP_ENV", "development")

_r: redis.Redis | None = None


def get_redis_client() -> redis.Redis:
    if _r is None:
        raise ValueError("Redis hasn't been initialized")
    return _r


@asynccontextmanager
async def lifespan(app: FastAPI):
    async with setup_redis():
        yield


app = FastAPI(lifespan=lifespan)
app.include_router(stocks_router)


def main():

    uvicorn.run(
        "main:app",
        host="127.0.0.1",
        port=8081,
        reload=True if app_env == "development" else False,
    )


if __name__ == "__main__":
    main()
