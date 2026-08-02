package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import org.junit.jupiter.api.Test
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WatchListFactoryImplTest {

    private val factory = WatchListFactoryImpl()

    @Test
    fun `creates service watchlist with correct fields`() {
        val user = xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User(
            id = "user-1",
            fullName = "Test User",
            email = "test@test.com"
        )

        val result = factory.createEmptyUserWatchList(
            watchListName = "My Watchlist",
            watchListDescription = "A test watchlist",
            user = user,
            visibility = WatchListVisibility.PRIVATE
        )

        assertEquals("My Watchlist", result.name)
        assertEquals("A test watchlist", result.description)
        assertEquals("user-1", result.createdBy)
        assertEquals(WatchListVisibility.PRIVATE, result.visibility)
        assertEquals(WatchListType.FIXED, result.type)
        assertNull(result.id)
        assertNull(result.createdAt)
        assertNull(result.updatedAt)
        assertNull(result.tickerSymbols)
        assertNull(result.screenQuery)
    }

    @Test
    fun `creates public watchlist`() {
        val user = xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User(
            id = "user-2",
            fullName = "Test User 2",
            email = "test2@test.com"
        )

        val result = factory.createEmptyUserWatchList(
            watchListName = "Public List",
            watchListDescription = "A public watchlist",
            user = user,
            visibility = WatchListVisibility.PUBLIC
        )

        assertEquals("Public List", result.name)
        assertEquals(WatchListVisibility.PUBLIC, result.visibility)
    }
}
