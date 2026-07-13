package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.repository.findByIdOrNull
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListRequestTickers
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetailsList
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass.WatchList as WatchListProto
import java.time.Instant
import java.util.Optional

class WatchListServiceV1UnitTest {

    private val watchListRepository = mock<WatchListRepository>()
    private val watchListTickerRepository = mock<WatchListTickerRepository>()
    private val watchListMapper = mock<WatchListMapper>()
    private val userRepository = mock<UserRepository>()
    private val stockClient = mock<StockClient>()
    private val watchListTxService = mock<WatchListTxService>()
    private val watchListCacheService = mock<WatchListCacheService>()
    private val tickerRepository = mock<TickerRepository>()

    private val service = WatchListServiceV1(
        watchListRepository = watchListRepository,
        watchListTickerRepository = watchListTickerRepository,
        watchListMapper = watchListMapper,
        userRepository = userRepository,
        stockClient = stockClient,
        watchListTxService = watchListTxService,
        watchListCacheService = watchListCacheService,
        tickerRepository = tickerRepository
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
            watchList = watchList,
            tickerCode = "AAPL"
        )
        val ticker2 = WatchListTicker(
            id = 2L,
            watchList = watchList,
            tickerCode = "GOOGL"
        )

        val tickersFromDb = listOf(ticker1, ticker2)
        val expectedTickerCodes = listOf("AAPL", "GOOGL")
        val tickerDetailsList = TickerDetailsList.newBuilder().build()
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
        val cachedWatchList = WatchListProto.newBuilder()
            .setId(watchListId)
            .setName("Test WatchList")
            .build()

        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(cachedWatchList)
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(tickersFromDb)
        whenever(stockClient.getBulkTickerDetails(expectedTickerCodes)).thenReturn(tickerDetailsList)
        whenever(watchListMapper.toWatchListResponse(any<WatchListProto>())).thenReturn(response)

        service.getWatchList(watchListId, GetWatchListRequest(tickerPageSize = 10, tickerPageNumber = 0))

        verify(stockClient).getBulkTickerDetails(expectedTickerCodes)
    }

    @Test
    fun `getWatchList throws NOT_FOUND when watchlist does not exist`() {
        whenever(watchListRepository.findById(999L)).thenReturn(Optional.empty())

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
            tickerDetails = TickerDetails(symbol = "AAPL", sector = "Technology", marketCap = 3_000_000_000_000L)
        )

        whenever(userRepository.findById(username)).thenReturn(Optional.of(user))
        whenever(tickerRepository.findAllById(any())).thenReturn(listOf(existingTicker))

        service.createWatchList(username, request)

        verify(stockClient, never()).getBulkTickerDetails(any())
        verify(watchListTxService).createWatchList(user, request, emptyList())
    }
}
