package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequestTickers
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListAggregate
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import java.time.Instant
import java.util.Optional

class WatchListServiceV1UnitTest {

    private val watchListRepository = mock<WatchListRepository>()
    private val watchListTickerRepository = mock<WatchListTickerRepository>()
    private val watchListMapper = mock<WatchListMapper>()
    private val userRepository = mock<UserRepository>()
    private val watchListFactory = mock<WatchListFactory>()
    private val watchListTxService = mock<WatchListTxService>()
    private val watchListCacheService = mock<WatchListCacheService>()
    private val stockService = mock<StockServiceV1>()
    private val objectMapper = mock<ObjectMapper>()

    private val service = WatchListServiceV1(
        watchListTickerRepository = watchListTickerRepository,
        watchListMapper = watchListMapper,
        userRepository = userRepository,
        watchListFactory = watchListFactory,
        watchListTxService = watchListTxService,
        watchListCacheService = watchListCacheService,
        stockService = stockService,
        objectMapper = objectMapper
    )

    @Test
    fun `getWatchList calls StockClient with ticker codes when tickers exist`() {
        val watchListId = 1L
        val watchList = WatchList(
            id = watchListId,
            name = "Test WatchList",
            description = "Test Description",
            createdBy = null,
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val ticker1 = WatchListTicker(
            id = 1L,
            watchList = DbWatchList(
                id = watchListId,
                name = "Test WatchList",
                description = "Test Description",
                createdBy = null,
                visibility = WatchListVisibility.PUBLIC,
                type = WatchListType.DYNAMIC
            ),
            tickerCode = "AAPL"
        )
        val ticker2 = WatchListTicker(
            id = 2L,
            watchList = DbWatchList(
                id = watchListId,
                name = "Test WatchList",
                description = "Test Description",
                createdBy = null,
                visibility = WatchListVisibility.PUBLIC,
                type = WatchListType.DYNAMIC
            ),
            tickerCode = "GOOGL"
        )

        val tickersFromDb = listOf(ticker1, ticker2)
        val expectedTickerCodes = listOf("AAPL", "GOOGL")
        val aggregate = WatchListAggregate(
            id = watchListId,
            name = "Test WatchList",
            description = "Test Description",
            createdBy = null,
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickers = null
        )
        val response = WatchListResponse(
            id = watchListId,
            name = "Test WatchList",
            description = "Test Description",
            createdBy = null,
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickers = null
        )

        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(watchList)
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(tickersFromDb)
        whenever(stockService.getBulkTickerDetails(expectedTickerCodes)).thenReturn(emptyList())
        whenever(watchListMapper.toWatchListAggregate(any(), any())).thenReturn(aggregate)
        whenever(watchListMapper.toWatchListResponse(aggregate)).thenReturn(response)

        service.getWatchList(watchListId, GetWatchListRequest(tickerPageSize = 10, tickerPageNumber = 0))

        verify(stockService).getBulkTickerDetails(expectedTickerCodes)
    }

    @Test
    fun `getWatchList throws NOT_FOUND when watchlist does not exist`() {
        whenever(watchListCacheService.findByIdOrNull(999L)).thenReturn(null)
        whenever(watchListTxService.findByIdOrNull(999L)).thenReturn(null)

        val exception = assertThrows<WatchListException> {
            service.getWatchList(999L, GetWatchListRequest(tickerPageSize = 10, tickerPageNumber = 0))
        }

        assert(exception.errorCode == WatchListExceptionErrorCode.NOT_FOUND)
    }

    @Test
    fun `createWatchList skips sidecar when catalog rows already exist`() {
        val username = "test-user"
        val user = User(id = username, email = "test@example.com", fullName = "Test User")
        val request = WatchListRequest(
            name = "Existing Tickers",
            description = "Test",
            visibility = WatchListVisibility.PRIVATE,
            tickers = listOf(WatchListRequestTickers(tickerCode = "AAPL", tickerExchange = "NASDAQ"))
        )
        val existingTicker = Ticker(
            tickerCode = "AAPL",
            tickerLongName = "Apple Inc.",
            tickerExchange = "NASDAQ",
            tickerDetails = """{"symbol":"AAPL","marketCap":3000000000000}"""
        )
        val tickerDetails = listOf(
            TickerDetails(
                symbol = "AAPL",
                exchange = "NASDAQ",
                tickerName = "Apple Inc.",
                industry = "Technology",
                marketCap = 3_000_000_000_000L,
                price = null
            )
        )
        val watchList = WatchList(
            id = 1L,
            name = "Existing Tickers",
            description = "Test",
            createdBy = username,
            visibility = WatchListVisibility.PRIVATE,
            type = WatchListType.FIXED,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickerSymbols = listOf("AAPL")
        )
        val aggregate = WatchListAggregate(
            id = 1L,
            name = "Existing Tickers",
            description = "Test",
            createdBy = username,
            visibility = WatchListVisibility.PRIVATE,
            type = WatchListType.FIXED,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickers = tickerDetails
        )
        val response = WatchListResponse(
            id = 1L,
            name = "Existing Tickers",
            description = "Test",
            createdBy = username,
            visibility = WatchListVisibility.PRIVATE,
            type = WatchListType.FIXED,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            tickers = null
        )

        whenever(userRepository.findById(username)).thenReturn(Optional.of(user))
        whenever(stockService.getBulkTickerDetails(listOf("AAPL"))).thenReturn(tickerDetails)
        whenever(objectMapper.writeValueAsString(any<TickerDetails>())).thenReturn(existingTicker.tickerDetails)
        whenever(watchListTxService.createWatchList(any(), any(), any())).thenReturn(watchList)
        whenever(watchListMapper.toWatchListAggregate(any(), any())).thenReturn(aggregate)
        whenever(watchListMapper.toWatchListResponse(aggregate)).thenReturn(response)

        service.createWatchList(username, request)

        verify(stockService).getBulkTickerDetails(listOf("AAPL"))
    }
}
