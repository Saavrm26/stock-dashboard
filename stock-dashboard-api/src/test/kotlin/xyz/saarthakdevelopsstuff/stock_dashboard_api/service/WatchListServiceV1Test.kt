package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListRequestTickers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@Import(TestContainersConfiguration::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WatchListServiceV1Test {

    @Autowired
    private lateinit var watchListServiceV1: WatchListServiceV1

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var watchListRepository: WatchListRepository

    @Autowired
    private lateinit var watchListTickerRepository: WatchListTickerRepository

    @MockBean
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    companion object {
        private const val TEST_USER_ID = "test-user-123"
        private const val TEST_USER_EMAIL = "test@example.com"
        private const val TEST_USER_NAME = "Test User"
    }

    @BeforeEach
    fun cleanUp() {
        watchListTickerRepository.deleteAll()
        watchListRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @Order(1)
    fun `createWatchList should save tickers when tickers are provided`() {
        userRepository.save(User(id = TEST_USER_ID, email = TEST_USER_EMAIL, fullName = TEST_USER_NAME))

        val request = WatchListRequest(
            name = "My Watchlist",
            description = "Test description",
            visibility = WatchListVisibility.PRIVATE,
            tickers = listOf(
                WatchListRequestTickers(tickerCode = "AAPL", tickerExchange = "NASDAQ"),
                WatchListRequestTickers(tickerCode = "GOOGL", tickerExchange = "NASDAQ")
            )
        )

        val response = watchListServiceV1.createWatchList(TEST_USER_ID, request)

        assertNotNull(response.id)

        val savedTickers = watchListTickerRepository.findAll()
        assertEquals(2, savedTickers.size)

        val appleTicker = savedTickers.find { it.tickerCode == "AAPL" }
        assertNotNull(appleTicker)
        assertEquals("NASDAQ", appleTicker.tickerExchange)
        assertEquals("", appleTicker.tickerLongName)
        assertEquals("AAPL", appleTicker.tickerDetails.symbol)

        val googleTicker = savedTickers.find { it.tickerCode == "GOOGL" }
        assertNotNull(googleTicker)
        assertEquals("NASDAQ", googleTicker.tickerExchange)
        assertEquals("", googleTicker.tickerLongName)
        assertEquals("GOOGL", googleTicker.tickerDetails.symbol)
    }

    @Test
    @Order(2)
    fun `createWatchList should save no tickers when tickers list is empty`() {
        userRepository.save(User(id = TEST_USER_ID, email = TEST_USER_EMAIL, fullName = TEST_USER_NAME))

        val request = WatchListRequest(
            name = "Empty Ticker Watchlist",
            description = "No tickers",
            visibility = WatchListVisibility.PRIVATE,
            tickers = emptyList()
        )

        val response = watchListServiceV1.createWatchList(TEST_USER_ID, request)

        assertNotNull(response.id)

        val allTickers = watchListTickerRepository.findAll()
        assertEquals(0, allTickers.size)
    }
}
