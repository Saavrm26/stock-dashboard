package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList as ServiceWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User as DbUser
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest
@Import(TestContainersConfiguration::class)
class WatchListMapperReverseMappingTest {

    @MockBean
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Autowired
    private lateinit var watchListMapper: WatchListMapper

    @Test
    fun `toDbWatchList maps service watchlist to db entity with provided user`() {
        val serviceWatchList = ServiceWatchList(
            id = null,
            name = "Test Watchlist",
            description = "Test description",
            createdBy = "user-1",
            visibility = WatchListVisibility.PRIVATE,
            type = WatchListType.FIXED,
            screenQuery = null,
            createdAt = null,
            updatedAt = null,
            tickerSymbols = null
        )

        val dbUser = DbUser(id = "user-1", email = "test@test.com", fullName = "Test User")

        val result = watchListMapper.toDbWatchList(serviceWatchList, dbUser)

        assertEquals("Test Watchlist", result.name)
        assertEquals("Test description", result.description)
        assertEquals(dbUser, result.createdBy)
        assertEquals(WatchListVisibility.PRIVATE, result.visibility)
        assertEquals(WatchListType.FIXED, result.type)
        assertNull(result.id)
        assertNull(result.screenQuery)
        assertNull(result.searchVector)
    }

    @Test
    fun `toDbWatchListTickers creates entities with null ids and correct watchlist ref`() {
        val dbWatchList = DbWatchList(
            id = 1L,
            name = "Test",
            description = "Desc",
            createdBy = null,
            visibility = WatchListVisibility.PRIVATE,
            type = WatchListType.FIXED
        )

        val tickerSymbols = listOf("AAPL", "GOOGL")
        val result = watchListMapper.toDbWatchListTickers(dbWatchList, tickerSymbols)

        assertEquals(2, result.size)
        assertNull(result[0].id)
        assertEquals(dbWatchList, result[0].watchList)
        assertEquals("AAPL", result[0].tickerCode)
        assertNull(result[1].id)
        assertEquals(dbWatchList, result[1].watchList)
        assertEquals("GOOGL", result[1].tickerCode)
    }

    @Test
    fun `toDbWatchListTickers returns empty list for empty symbols`() {
        val dbWatchList = DbWatchList(
            id = 1L,
            name = "Test",
            description = "Desc",
            createdBy = null,
            visibility = WatchListVisibility.PRIVATE,
            type = WatchListType.FIXED
        )

        val result = watchListMapper.toDbWatchListTickers(dbWatchList, emptyList())
        assertEquals(0, result.size)
    }
}
