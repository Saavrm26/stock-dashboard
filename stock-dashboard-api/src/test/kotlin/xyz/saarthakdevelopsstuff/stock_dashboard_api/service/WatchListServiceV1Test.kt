package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.mockito.kotlin.whenever
import org.junit.jupiter.api.Assertions.assertThrows
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequestTickers
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientException
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails as ProtoTickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetailsList
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

    @Autowired
    private lateinit var tickerRepository: TickerRepository

    @MockBean
    private lateinit var stockClient: StockClient

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, ByteArray>

    @Autowired
    @Qualifier("setRedisTemplate")
    private lateinit var setRedisTemplate: RedisTemplate<String, String>

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
        tickerRepository.deleteAll()
        userRepository.deleteAll()
        redisTemplate.connectionFactory?.connection?.flushAll()
        setRedisTemplate.connectionFactory?.connection?.flushAll()
    }

    @Test
    @Order(1)
    fun `createWatchList resolves missing catalog tickers from the sidecar`() {
        userRepository.save(User(id = TEST_USER_ID, email = TEST_USER_EMAIL, fullName = TEST_USER_NAME))

        whenever(stockClient.getBulkTickerDetails(listOf("AAPL", "GOOGL"))).thenReturn(
            TickerDetailsList.newBuilder()
                .addTickers(
                    ProtoTickerDetails.newBuilder()
                        .setSymbol("AAPL")
                        .setShortName("Apple Inc.")
                        .setExchange("NASDAQ")
                        .setSector("Technology")
                        .setMarketCap(3_000_000_000_000L)
                        .build()
                )
                .addTickers(
                    ProtoTickerDetails.newBuilder()
                        .setSymbol("GOOGL")
                        .setShortName("Alphabet Inc.")
                        .setExchange("NASDAQ")
                        .setSector("Technology")
                        .setMarketCap(2_000_000_000_000L)
                        .build()
                )
                .build()
        )

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

        assertEquals(2, tickerRepository.count())
        assertEquals("Apple Inc.", tickerRepository.findById("AAPL").get().tickerLongName)
        assertEquals(2, watchListTickerRepository.count())
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

    @Test
    @Order(3)
    fun `createWatchList dedupes repeated ticker codes into a single link row`() {
        userRepository.save(User(id = TEST_USER_ID, email = TEST_USER_EMAIL, fullName = TEST_USER_NAME))

        whenever(stockClient.getBulkTickerDetails(listOf("AAPL"))).thenReturn(
            TickerDetailsList.newBuilder()
                .addTickers(
                    ProtoTickerDetails.newBuilder()
                        .setSymbol("AAPL")
                        .setShortName("Apple Inc.")
                        .setExchange("NASDAQ")
                        .setSector("Technology")
                        .setMarketCap(3_000_000_000_000L)
                        .build()
                )
                .build()
        )

        val request = WatchListRequest(
            name = "Duplicate Ticker Watchlist",
            description = "Repeated code",
            visibility = WatchListVisibility.PRIVATE,
            tickers = listOf(
                WatchListRequestTickers(tickerCode = "AAPL", tickerExchange = "NASDAQ"),
                WatchListRequestTickers(tickerCode = "AAPL", tickerExchange = "NASDAQ")
            )
        )

        val response = watchListServiceV1.createWatchList(TEST_USER_ID, request)

        assertNotNull(response.id)
        assertEquals(1, watchListTickerRepository.count())
    }

    @Test
    @Order(4)
    fun `createWatchList fails before any write when the sidecar omits a missing code`() {
        userRepository.save(User(id = TEST_USER_ID, email = TEST_USER_EMAIL, fullName = TEST_USER_NAME))

        whenever(stockClient.getBulkTickerDetails(listOf("AAPL", "GOOGL"))).thenReturn(
            TickerDetailsList.newBuilder()
                .addTickers(
                    ProtoTickerDetails.newBuilder()
                        .setSymbol("AAPL")
                        .setShortName("Apple Inc.")
                        .setExchange("NASDAQ")
                        .setSector("Technology")
                        .setMarketCap(3_000_000_000_000L)
                        .build()
                )
                .build()
        )

        val request = WatchListRequest(
            name = "Partial Response Watchlist",
            description = "GOOGL unresolved",
            visibility = WatchListVisibility.PRIVATE,
            tickers = listOf(
                WatchListRequestTickers(tickerCode = "AAPL", tickerExchange = "NASDAQ"),
                WatchListRequestTickers(tickerCode = "GOOGL", tickerExchange = "NASDAQ")
            )
        )

        assertThrows(StockClientException::class.java) {
            watchListServiceV1.createWatchList(TEST_USER_ID, request)
        }

        assertEquals(0, tickerRepository.count())
        assertEquals(0, watchListRepository.count())
        assertEquals(0, watchListTickerRepository.count())
    }
}
