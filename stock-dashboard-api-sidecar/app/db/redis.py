from contextlib import asynccontextmanager
import os
import redis.asyncio as redis


app_env = os.getenv("APP_ENV", "development")

_r: redis.Redis | None = None


def get_redis_client() -> redis.Redis:
    global _r
    if _r is not None:
        return _r
    raise ValueError("Redis wasn't initialized")


@asynccontextmanager
async def setup_redis():
    global _r
    if app_env == "development":
        _r = redis.Redis(host="localhost", port=6379, decode_responses=False)
    else:
        REDIS_HOST = os.environ.get("REDIS_HOST") or ""
        REDIS_PORT = os.environ.get("REDIS_PORT")
        REDIS_PORT = int(REDIS_PORT) if REDIS_PORT else None
        REDIS_PASSWD = os.environ.get("REDIS_PASSWD") or ""
        if not REDIS_HOST or not REDIS_PORT or not REDIS_PASSWD:
            raise ValueError(
                "One or more env redis connection env variables is missing"
            )
        _r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, password=REDIS_PASSWD, decode_responses=False)
    yield _r
    await _r.aclose()
