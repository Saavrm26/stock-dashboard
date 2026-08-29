package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.StockDashboardOAuth2UserService
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.CreateWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.CreateWatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListAggregate
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import java.time.Instant

class WatchListServiceV1UnitTest {

    private val watchListRepository = mock<WatchListRepository>()
    private val watchListTickerRepository = mock<WatchListTickerRepository>()
    private val watchListMapper = mock<WatchListMapper>()
    private val stockDashboardOAuth2UserService = mock<StockDashboardOAuth2UserService>()
    private val watchListFactory = mock<WatchListFactory>()
    private val watchListTxService = mock<WatchListTxService>()
    private val watchListCacheService = mock<WatchListCacheService>()
    private val stockService = mock<StockServiceV1>()
    private val objectMapper = mock<ObjectMapper>()

    private val service = WatchListServiceV1(
        watchListTickerRepository = watchListTickerRepository,
        watchListMapper = watchListMapper,
        stockDashboardOAuth2UserService = stockDashboardOAuth2UserService,
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
        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(watchList)
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(tickersFromDb)
        whenever(stockService.getBulkTickerDetails(expectedTickerCodes)).thenReturn(emptyList())
        whenever(watchListMapper.toWatchListAggregate(any(), any())).thenReturn(aggregate)

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
        val serviceUser = User(id = username, fullName = "Test User", email = "test@example.com")
        val createWatchList = CreateWatchList(
            name = "Existing Tickers",
            description = "Test",
            visibility = WatchListVisibility.PRIVATE,
            tickers = listOf(CreateWatchListTicker(tickerCode = "AAPL", tickerExchange = "NASDAQ"))
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
        whenever(stockDashboardOAuth2UserService.getUser(username)).thenReturn(serviceUser)
        whenever(stockService.getBulkTickerDetails(listOf("AAPL"))).thenReturn(tickerDetails)
        whenever(watchListTxService.createWatchList(any(), any(), any())).thenReturn(watchList)

        service.createWatchList(username, createWatchList)

        verify(stockService).getBulkTickerDetails(listOf("AAPL"))
    }

    @Test
    fun `addTickersToWatchList throws UNAUTHORIZED when requester is not the owner`() {
        val watchListId = 1L
        val watchList = WatchList(
            id = watchListId, name = "Test", description = "Test", createdBy = "owner",
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = Instant.now(), updatedAt = Instant.now()
        )
        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(watchList)

        val exception = assertThrows<WatchListException> {
            service.addTickersToWatchList(watchListId, "not-the-owner", listOf("AAPL"))
        }

        assert(exception.errorCode == WatchListExceptionErrorCode.UNAUTHORIZED)
    }

    @Test
    fun `addTickersToWatchList resolves only tickers not already on the watchlist`() {
        val watchListId = 1L
        val username = "owner"
        val watchList = WatchList(
            id = watchListId, name = "Test", description = "Test", createdBy = username,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = Instant.now(), updatedAt = Instant.now()
        )
        val existingTicker = WatchListTicker(
            id = 1L,
            watchList = DbWatchList(
                id = watchListId, name = "Test", description = "Test", createdBy = null,
                visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED
            ),
            tickerCode = "AAPL"
        )
        val newTickerDetails = listOf(
            TickerDetails(symbol = "MSFT", exchange = "NASDAQ", tickerName = "Microsoft", industry = null, marketCap = null, price = null)
        )
        val updatedWatchList = watchList.copy(tickerSymbols = listOf("AAPL", "MSFT"))

        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(watchList)
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(listOf(existingTicker))
        whenever(stockService.getBulkTickerDetails(listOf("MSFT"))).thenReturn(newTickerDetails)
        whenever(watchListTxService.addTickersToWatchList(watchListId, newTickerDetails)).thenReturn(updatedWatchList)

        val result = service.addTickersToWatchList(watchListId, username, listOf("AAPL", "MSFT"))

        verify(stockService).getBulkTickerDetails(listOf("MSFT"))
        verify(watchListCacheService).evict(watchListId)
        assert(result == updatedWatchList)
    }

    @Test
    fun `removeTickersFromWatchList throws UNAUTHORIZED when requester is not the owner`() {
        val watchListId = 1L
        val watchList = WatchList(
            id = watchListId, name = "Test", description = "Test", createdBy = "owner",
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = Instant.now(), updatedAt = Instant.now()
        )
        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(watchList)

        val exception = assertThrows<WatchListException> {
            service.removeTickersFromWatchList(watchListId, "not-the-owner", listOf("AAPL"))
        }

        assert(exception.errorCode == WatchListExceptionErrorCode.UNAUTHORIZED)
    }

    @Test
    fun `removeTickersFromWatchList delegates to txService and evicts cache`() {
        val watchListId = 1L
        val username = "owner"
        val watchList = WatchList(
            id = watchListId, name = "Test", description = "Test", createdBy = username,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = Instant.now(), updatedAt = Instant.now()
        )
        val updatedWatchList = watchList.copy(tickerSymbols = emptyList())

        whenever(watchListCacheService.findByIdOrNull(watchListId)).thenReturn(watchList)
        whenever(watchListTxService.removeTickersFromWatchList(watchListId, listOf("AAPL"))).thenReturn(updatedWatchList)

        val result = service.removeTickersFromWatchList(watchListId, username, listOf("AAPL"))

        verify(watchListTxService).removeTickersFromWatchList(watchListId, listOf("AAPL"))
        verify(watchListCacheService).evict(watchListId)
        assert(result == updatedWatchList)
    }
}
