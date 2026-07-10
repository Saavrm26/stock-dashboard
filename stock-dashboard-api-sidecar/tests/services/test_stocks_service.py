"""Tests for StocksService bulk_get_ticker_details caching behavior."""

import pytest
from unittest.mock import AsyncMock, MagicMock, patch
from app.services.stocks_service import StocksService
from app.models.generated.ticker_details_pb2 import TickerDetails


@pytest.fixture
def mock_redis():
    """Mock Redis client returning bytes (decode_responses=False)."""
    return AsyncMock()


@pytest.fixture
def stocks_service(mock_redis):
    """Create StocksService with mocked Redis client."""
    with patch("app.services.stocks_service.get_redis_client", return_value=mock_redis):
        yield StocksService()


@pytest.mark.asyncio
async def test_bulk_get_ticker_details_cache_miss_all(stocks_service, mock_redis):
    """All tickers miss cache - should fetch from yfinance and populate cache."""
    tickers = ["AAPL", "GOOGL"]

    # Mock Redis MGET returns all None (cache miss)
    mock_redis.mget.return_value = [None, None]

    # Mock yfinance Tickers response
    mock_ticker_aapl = MagicMock()
    mock_ticker_aapl.info = {"symbol": "AAPL", "shortName": "Apple Inc."}

    mock_ticker_googl = MagicMock()
    mock_ticker_googl.info = {"symbol": "GOOGL", "shortName": "Alphabet Inc."}

    mock_multi = MagicMock()
    mock_multi.tickers = {
        "AAPL": mock_ticker_aapl,
        "GOOGL": mock_ticker_googl,
    }

    with patch("app.services.stocks_service.asyncio.to_thread", return_value=mock_multi):
        result = await stocks_service.bulk_get_ticker_details(tickers)

    assert len(result) == 2
    assert result[0].symbol == "AAPL"
    assert result[1].symbol == "GOOGL"

    # Verify MGET called with correct keys
    mock_redis.mget.assert_called_once_with(
        "sidecar:ticker:AAPL", "sidecar:ticker:GOOGL"
    )

    # Verify MSET called to populate cache
    mock_redis.mset.assert_called_once()
    args, kwargs = mock_redis.mset.call_args
    mapping = args[0] if args else kwargs.get("mapping", {})
    assert len(mapping) == 2  # Two ticker entries
    assert "sidecar:ticker:AAPL" in mapping
    assert "sidecar:ticker:GOOGL" in mapping

    # Verify TTL is set via expire() calls (mset doesn't support EX)
    assert mock_redis.expire.call_count == 2
    for call in mock_redis.expire.call_args_list:
        args, _ = call
        assert args[1] == 300  # TTL = 300 seconds


@pytest.mark.asyncio
async def test_bulk_get_ticker_details_cache_hit_all(stocks_service, mock_redis):
    """All tickers hit cache - should return from Redis without yfinance call."""
    tickers = ["AAPL", "GOOGL"]

    # Create protobuf bytes for cached data
    aapl_pb = TickerDetails(symbol="AAPL", short_name="Apple Inc.")
    googl_pb = TickerDetails(symbol="GOOGL", short_name="Alphabet Inc.")

    mock_redis.mget.return_value = [
        aapl_pb.SerializeToString(),
        googl_pb.SerializeToString(),
    ]

    with patch("app.services.stocks_service.asyncio.to_thread") as mock_to_thread:
        result = await stocks_service.bulk_get_ticker_details(tickers)

        # yfinance should NOT be called
        mock_to_thread.assert_not_called()

    assert len(result) == 2
    assert result[0].symbol == "AAPL"
    assert result[1].symbol == "GOOGL"

    mock_redis.mget.assert_called_once_with(
        "sidecar:ticker:AAPL", "sidecar:ticker:GOOGL"
    )
    mock_redis.mset.assert_not_called()
    mock_redis.expire.assert_not_called()


@pytest.mark.asyncio
async def test_bulk_get_ticker_details_cache_partial_hit(stocks_service, mock_redis):
    """Partial cache hit - some from cache, some from yfinance."""
    tickers = ["AAPL", "GOOGL", "MSFT"]

    # AAPL cached, GOOGL and MSFT not cached
    aapl_pb = TickerDetails(symbol="AAPL", short_name="Apple Inc.")
    mock_redis.mget.return_value = [
        aapl_pb.SerializeToString(),  # AAPL hit
        None,  # GOOGL miss
        None,  # MSFT miss
    ]

    mock_ticker_googl = MagicMock()
    mock_ticker_googl.info = {"symbol": "GOOGL", "shortName": "Alphabet Inc."}
    mock_ticker_msft = MagicMock()
    mock_ticker_msft.info = {"symbol": "MSFT", "shortName": "Microsoft Corp."}

    mock_multi = MagicMock()
    mock_multi.tickers = {
        "GOOGL": mock_ticker_googl,
        "MSFT": mock_ticker_msft,
    }

    with patch("app.services.stocks_service.asyncio.to_thread", return_value=mock_multi):
        result = await stocks_service.bulk_get_ticker_details(tickers)

    assert len(result) == 3
    assert result[0].symbol == "AAPL"
    assert result[1].symbol == "GOOGL"
    assert result[2].symbol == "MSFT"

    # MSET should only be called for GOOGL and MSFT
    mock_redis.mset.assert_called_once()
    args, kwargs = mock_redis.mset.call_args
    mapping = args[0] if args else kwargs.get("mapping", {})
    assert "sidecar:ticker:GOOGL" in mapping
    assert "sidecar:ticker:MSFT" in mapping
    assert "sidecar:ticker:AAPL" not in mapping

    # Verify TTL is set via expire() for the two new entries
    assert mock_redis.expire.call_count == 2
    for call in mock_redis.expire.call_args_list:
        args, _ = call
        assert args[1] == 300


@pytest.mark.asyncio
async def test_bulk_get_ticker_details_empty_list(stocks_service, mock_redis):
    """Empty ticker list returns empty list, no Redis calls."""
    result = await stocks_service.bulk_get_ticker_details([])
    assert result == []
    mock_redis.mget.assert_not_called()
    mock_redis.mset.assert_not_called()
    mock_redis.expire.assert_not_called()


@pytest.mark.asyncio
async def test_bulk_get_ticker_details_preserves_order(stocks_service, mock_redis):
    """Result order matches input ticker order."""
    tickers = ["GOOGL", "AAPL", "MSFT"]

    mock_redis.mget.return_value = [None, None, None]

    mock_tickers = {}
    for t in tickers:
        m = MagicMock()
        m.info = {"symbol": t, "shortName": f"{t} Inc."}
        mock_tickers[t] = m

    mock_multi = MagicMock()
    mock_multi.tickers = mock_tickers

    with patch("app.services.stocks_service.asyncio.to_thread", return_value=mock_multi):
        result = await stocks_service.bulk_get_ticker_details(tickers)

    assert [r.symbol for r in result] == ["GOOGL", "AAPL", "MSFT"]


@pytest.mark.asyncio
async def test_bulk_get_ticker_details_deduplicates(stocks_service, mock_redis):
    """Duplicate tickers in input handled correctly."""
    tickers = ["AAPL", "AAPL", "GOOGL"]

    aapl_bytes = TickerDetails(symbol="AAPL", short_name="Apple Inc.").SerializeToString()
    googl_bytes = TickerDetails(symbol="GOOGL", short_name="Alphabet Inc.").SerializeToString()
    mock_redis.mget.return_value = [aapl_bytes, aapl_bytes, googl_bytes]
    mock_redis.mset = AsyncMock()

    with patch("app.services.stocks_service.asyncio.to_thread") as mock_to_thread:
        result = await stocks_service.bulk_get_ticker_details(tickers)

    assert len(result) == 3
    assert result[0].symbol == "AAPL"
    assert result[1].symbol == "AAPL"
    assert result[2].symbol == "GOOGL"
    mock_to_thread.assert_not_called()
