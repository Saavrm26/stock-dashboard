package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import java.time.Instant

class WatchListResponseTest {

    @Test
    fun `WatchListResponse can hold tickers list`() {
        val tickerDetails = TickerDetailsResponse(
            symbol = "AAPL",
            exchange = "NASDAQ",
            tickerName = "Apple Inc.",
            industry = "Technology",
            marketCap = 3_000_000_000_000L,
            price = 150.0
        )

        val response = WatchListResponse(
            id = 1L,
            name = "Test WatchList",
            description = "Test Description",
            createdBy = "user",
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickers = listOf(tickerDetails)
        )

        assertEquals(1, response.tickers?.size)
        assertEquals("AAPL", response.tickers?.first()?.symbol)
    }

    @Test
    fun `WatchListResponse tickers can be null for backward compatibility`() {
        val response = WatchListResponse(
            id = 1L,
            name = "Test WatchList",
            description = "Test Description",
            createdBy = "user",
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickers = null
        )

        assertNull(response.tickers)
    }
}
