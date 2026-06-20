package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListVisibility
import java.time.Instant

class WatchListResponseTest {

    @Test
    fun `WatchListResponse can hold ticker symbols list`() {
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
            tickerSymbols = listOf("AAPL")
        )

        assertEquals(1, response.tickerSymbols?.size)
        assertEquals("AAPL", response.tickerSymbols?.first())
    }

    @Test
    fun `WatchListResponse tickerSymbols can be null for backward compatibility`() {
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
            tickerSymbols = null
        )

        assertNull(response.tickerSymbols)
    }
}
