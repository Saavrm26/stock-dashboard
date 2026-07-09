package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails
import java.time.Instant

class WatchListResponseTest {

    @Test
    fun `WatchListResponse can hold tickers list`() {
        val tickerDetails = TickerDetails.newBuilder()
            .setSymbol("AAPL")
            .setSector("Technology")
            .setMarketCap(3000000000000L)
            .build()

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
