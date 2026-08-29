"""
Integration tests for StocksService with real Redis.

Requires Redis running on localhost:6379 (Docker).
Run with: TEST_REDIS=true uv run pytest tests/integration/test_stocks_service_integration.py -v

Docker: docker run -d -p 6379:6379 redis:7-alpine
"""

import os
import pytest
import pytest_asyncio
import yfinance as yf

from app.db.redis import setup_redis
from app.services.stocks_service import StocksService


# Skip all tests in this module unless TEST_REDIS=true is set
pytestmark = pytest.mark.skipif(
    os.getenv("TEST_REDIS", "").lower() != "true",
    reason="TEST_REDIS=true not set. Run with: TEST_REDIS=true uv run pytest tests/integration/test_stocks_service_integration.py -v"
)


@pytest_asyncio.fixture
async def redis_client():
    """Provide a real Redis client via the app's setup_redis context manager."""
    async with setup_redis() as client:
        # Clear any existing test data before each test
        await client.flushdb()
        yield client
        # Cleanup after test
        await client.flushdb()


@pytest_asyncio.fixture
async def stocks_service(redis_client):
    """Create a StocksService instance using the real Redis client."""
    # The service uses get_redis_client() internally which returns the global _r
    # Since setup_redis() has initialized the global _r, the service will use it
    service = StocksService()
    return service


class TestStocksServiceCacheIntegration:
    """Integration tests verifying Redis cache behavior with real Redis."""

    @pytest.mark.asyncio
    async def test_bulk_get_ticker_details_cache_miss_then_hit(self, stocks_service, redis_client):
        """
        Test that:
        1. First call to bulk_get_ticker_details is a cache miss (calls yfinance)
        2. Second call is a cache hit (returns from Redis)
        3. Cache is populated after first call with correct keys and TTL
        """
        tickers = ["AAPL", "MSFT"]
        
        # Verify cache is empty initially
        cache_keys_before = await redis_client.keys("sidecar:ticker:*")
        assert len(cache_keys_before) == 0, "Cache should be empty before test"
        
        # First call - should be cache miss (calls yfinance)
        details_first = await stocks_service.bulk_get_ticker_details(tickers)
        
        # Verify cache was populated after first call
        cache_keys_after_first = await redis_client.keys("sidecar:ticker:*")
        assert len(cache_keys_after_first) == len(tickers), \
            f"Expected {len(tickers)} cache keys after first call, got {len(cache_keys_after_first)}"
        
        # Verify each ticker has a cache entry with correct key format
        for ticker in tickers:
            key = f"sidecar:ticker:{ticker.upper()}"
            exists = await redis_client.exists(key)
            assert exists == 1, f"Cache key {key} should exist after first call"
            
            # Verify TTL is set (should be ~300 seconds = 5 minutes)
            ttl = await redis_client.ttl(key)
            assert ttl > 0, f"Cache key {key} should have TTL > 0"
            assert ttl <= 300, f"TTL should be <= 300s, got {ttl}"
            assert ttl >= 290, f"TTL should be close to 300s, got {ttl}"
        
        # Second call - should be cache hit (returns from Redis)
        details_second = await stocks_service.bulk_get_ticker_details(tickers)
        
        # Verify results are consistent (both calls return same data structure)
        assert len(details_first) == len(details_second) == len(tickers)
        for first, second in zip(details_first, details_second):
            assert first.symbol == second.symbol
            # Cache hit should return identical data
            assert first == second
        
        # Verify cache keys still exist (not evicted)
        cache_keys_after_second = await redis_client.keys("sidecar:ticker:*")
        assert len(cache_keys_after_second) == len(tickers)

    @pytest.mark.asyncio
    async def test_get_ticker_details_cache_miss_then_hit(self, stocks_service, redis_client):
        """
        Test single ticker details cache behavior:
        1. First call is cache miss
        2. Second call is cache hit
        3. Cache is populated after first call with TTL
        """
        ticker = "AAPL"
        cache_key = f"sidecar:ticker:{ticker.upper()}"
        
        # Verify cache is empty initially
        exists_before = await redis_client.exists(cache_key)
        assert exists_before == 0, "Cache should be empty before first call"
        
        # First call - cache miss
        detail_first = await stocks_service.get_ticker_details(ticker)
        
        # Verify cache populated
        exists_after_first = await redis_client.exists(cache_key)
        assert exists_after_first == 1, f"Cache key {cache_key} should exist after first call"
        
        # Verify TTL is set (5 minutes = 300 seconds)
        ttl = await redis_client.ttl(cache_key)
        assert ttl > 0, f"Cache key {cache_key} should have TTL > 0"
        assert ttl <= 300, f"TTL should be <= 300s, got {ttl}"
        assert ttl >= 290, f"TTL should be close to 300s, got {ttl}"
        
        # Second call - cache hit
        detail_second = await stocks_service.get_ticker_details(ticker)
        
        # Verify results match
        assert detail_first.symbol == detail_second.symbol
        assert detail_first == detail_second
        
        # Cache still exists
        exists_after_second = await redis_client.exists(cache_key)
        assert exists_after_second == 1

    @pytest.mark.asyncio
    async def test_cache_keys_have_correct_format_and_ttl(self, stocks_service, redis_client):
        """Verify cache keys follow expected naming convention and have TTL."""
        tickers = ["AAPL", "MSFT", "GOOGL"]
        
        # Populate cache
        await stocks_service.bulk_get_ticker_details(tickers)
        
        # Check key format and TTL for each
        for ticker in tickers:
            expected_key = f"sidecar:ticker:{ticker.upper()}"
            exists = await redis_client.exists(expected_key)
            assert exists == 1, f"Expected key {expected_key} to exist"
            
            # Verify TTL
            ttl = await redis_client.ttl(expected_key)
            assert ttl > 0, f"Key {expected_key} should have TTL > 0"
            assert ttl <= 300, f"TTL should be <= 300s"
            assert ttl >= 290, f"TTL should be close to 300s"
        
        # Verify no unexpected keys
        all_keys = await redis_client.keys("sidecar:ticker:*")
        assert len(all_keys) == len(tickers)
        for key in all_keys:
            key_str = key.decode() if isinstance(key, bytes) else key
            assert key_str in [f"sidecar:ticker:{t.upper()}" for t in tickers]

    @pytest.mark.asyncio
    async def test_different_tickers_have_separate_cache_entries(self, stocks_service, redis_client):
        """Verify each ticker gets its own cache entry."""
        tickers = ["AAPL", "MSFT", "GOOGL", "TSLA", "AMZN"]
        
        await stocks_service.bulk_get_ticker_details(tickers)
        
        # Each ticker should have its own key
        for ticker in tickers:
            key = f"sidecar:ticker:{ticker.upper()}"
            exists = await redis_client.exists(key)
            assert exists == 1, f"Missing cache entry for {ticker}"
        
        # Total keys should match
        all_keys = await redis_client.keys("sidecar:ticker:*")
        assert len(all_keys) == len(tickers)

    @pytest.mark.asyncio
    async def test_cache_persists_across_service_instances(self, redis_client):
        """
        Verify cache persists across different StocksService instances
        (simulating multiple requests to the same Redis).
        """
        tickers = ["AAPL", "MSFT"]
        
        # First service instance - populate cache
        service1 = StocksService()
        await service1.bulk_get_ticker_details(tickers)
        
        # Verify cache populated
        for ticker in tickers:
            key = f"sidecar:ticker:{ticker.upper()}"
            assert await redis_client.exists(key) == 1
        
        # Second service instance - should hit cache
        service2 = StocksService()
        details = await service2.bulk_get_ticker_details(tickers)
        
        # Results should come from cache (same data)
        assert len(details) == len(tickers)
        for detail in details:
            assert detail.symbol in [t.upper() for t in tickers]

    @pytest.mark.asyncio
    async def test_bulk_get_with_duplicates_handles_cache_correctly(self, stocks_service, redis_client):
        """Test that duplicate tickers in input are handled correctly with cache."""
        tickers = ["AAPL", "AAPL", "GOOGL"]  # AAPL duplicated
        
        # First call
        result_first = await stocks_service.bulk_get_ticker_details(tickers)
        
        # Verify cache has entries for unique tickers only
        cache_keys = await redis_client.keys("sidecar:ticker:*")
        assert len(cache_keys) == 2  # AAPL and GOOGL only
        
        # Second call - should hit cache for both
        result_second = await stocks_service.bulk_get_ticker_details(tickers)
        
        # Results should match
        assert len(result_first) == len(result_second) == 3
        assert [r.symbol for r in result_first] == [r.symbol for r in result_second]
        # First two should be AAPL (same object from cache)
        assert result_second[0].symbol == "AAPL"
        assert result_second[1].symbol == "AAPL"
        assert result_second[2].symbol == "GOOGL"

    @pytest.mark.asyncio
    async def test_partial_cache_hit_in_bulk_get(self, stocks_service, redis_client):
        """
        Test bulk_get with some tickers cached and some not.
        This is hard to test deterministically with real yfinance,
        but we can at least verify cache population behavior.
        """
        tickers = ["AAPL", "MSFT"]
        
        # First call populates cache for both
        await stocks_service.bulk_get_ticker_details(tickers)
        
        # Verify both cached
        for ticker in tickers:
            key = f"sidecar:ticker:{ticker.upper()}"
            assert await redis_client.exists(key) == 1
        
        # Now manually delete one cache entry to simulate partial cache
        await redis_client.delete("sidecar:ticker:MSFT")
        
        # Verify one is missing
        assert await redis_client.exists("sidecar:ticker:AAPL") == 1
        assert await redis_client.exists("sidecar:ticker:MSFT") == 0
        
        # Call again - should fetch MSFT from yfinance, AAPL from cache
        result = await stocks_service.bulk_get_ticker_details(tickers)
        
        # Both should be in result
        assert len(result) == 2
        symbols = [r.symbol for r in result]
        assert "AAPL" in symbols
        assert "MSFT" in symbols
        
        # Both should be cached again
        assert await redis_client.exists("sidecar:ticker:AAPL") == 1
        assert await redis_client.exists("sidecar:ticker:MSFT") == 1

    @pytest.mark.asyncio
    async def test_empty_ticker_list_returns_empty_no_redis_calls(self, stocks_service, redis_client):
        """Empty ticker list returns empty list without touching Redis."""
        result = await stocks_service.bulk_get_ticker_details([])
        assert result == []
        
        # No cache keys should be created
        cache_keys = await redis_client.keys("sidecar:ticker:*")
        assert len(cache_keys) == 0

    @pytest.mark.asyncio
    async def test_case_insensitive_ticker_handling(self, stocks_service, redis_client):
        """Verify ticker case is normalized in cache keys."""
        tickers = ["aapl", "MsFt", "GOOGL"]
        
        await stocks_service.bulk_get_ticker_details(tickers)
        
        # All keys should be uppercase
        cache_keys = await redis_client.keys("sidecar:ticker:*")
        assert len(cache_keys) == 3
        
        for key in cache_keys:
            key_str = key.decode() if isinstance(key, bytes) else key
            assert key_str == key_str.upper(), f"Key {key_str} should be uppercase"
        
        expected_keys = {"sidecar:ticker:AAPL", "sidecar:ticker:MSFT", "sidecar:ticker:GOOGL"}
        actual_keys = {k.decode() if isinstance(k, bytes) else k for k in cache_keys}
        assert actual_keys == expected_keys


class TestStocksServiceRedisConnection:
    """Tests verifying Redis connection and setup behavior."""

    @pytest.mark.asyncio
    async def test_redis_connection_works(self, redis_client):
        """Verify we can connect to Redis and perform basic operations."""
        # Ping
        pong = await redis_client.ping()
        assert pong is True
        
        # Set/get
        await redis_client.set("test:key", "test:value")
        value = await redis_client.get("test:key")
        assert value == b"test:value"
        
        # Cleanup
        await redis_client.delete("test:key")

    @pytest.mark.asyncio
    async def test_setup_redis_context_manager_lifecycle(self):
        """Verify setup_redis properly initializes and cleans up."""
        # Use setup_redis directly
        async with setup_redis() as client:
            assert client is not None
            pong = await client.ping()
            assert pong is True
            await client.set("lifecycle:test", "value")
            val = await client.get("lifecycle:test")
            assert val == b"value"
        # After context exit, client should be closed
        # (We can't easily test closed state without accessing private attr)
