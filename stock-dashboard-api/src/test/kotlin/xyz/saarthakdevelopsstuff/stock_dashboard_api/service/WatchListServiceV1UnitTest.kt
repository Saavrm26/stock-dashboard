package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetailsList
import java.time.Instant
import java.util.Optional

class WatchListServiceV1UnitTest {

    private val watchListRepository = mock<WatchListRepository>()
    private val watchListTickerRepository = mock<WatchListTickerRepository>()
    private val watchListMapper = mock<WatchListMapper>()
    private val userRepository = mock<UserRepository>()
    private val watchListFactory = mock<WatchListFactory>()
    private val stockClient = mock<StockClient>()

    private val service = WatchListServiceV1(
        watchListRepository = watchListRepository,
        watchListTickerRepository = watchListTickerRepository,
        watchListMapper = watchListMapper,
        userRepository = userRepository,
        watchListFactory = watchListFactory,
        stockClient = stockClient
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
            tickerCode = "AAPL",
            tickerLongName = "Apple Inc.",
            tickerExchange = "NASDAQ",
            tickerDetails = TickerDetails(symbol = "AAPL", sector = null, marketCap = null),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val ticker2 = WatchListTicker(
            id = 2L,
            watchList = watchList,
            tickerCode = "GOOGL",
            tickerLongName = "Alphabet Inc.",
            tickerExchange = "NASDAQ",
            tickerDetails = TickerDetails(symbol = "GOOGL", sector = null, marketCap = null),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
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

        whenever(watchListRepository.findById(watchListId)).thenReturn(Optional.of(watchList))
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(tickersFromDb)
        whenever(stockClient.getBulkTickerDetails(expectedTickerCodes)).thenReturn(tickerDetailsList)
        whenever(watchListMapper.toWatchListResponse(watchList)).thenReturn(response)

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
}
