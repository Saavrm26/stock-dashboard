package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User as DbUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User as ServiceUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList as ServiceWatchList
import kotlin.test.assertEquals
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class WatchListTxServiceTest {

    @Mock
    private lateinit var watchListRepository: WatchListRepository

    @Mock
    private lateinit var watchListMapper: WatchListMapper

    @Mock
    private lateinit var tickerMapper: TickerMapper

    @Mock
    private lateinit var tickerRepository: TickerRepository

    @Mock
    private lateinit var watchListTickerRepository: WatchListTickerRepository

    private lateinit var watchListTxService: WatchListTxService

    @BeforeEach
    fun setUp() {
        watchListTxService = WatchListTxService(
            watchListRepository = watchListRepository,
            watchListMapper = watchListMapper,
            tickerMapper = tickerMapper,
            tickerRepository = tickerRepository,
            watchListTickerRepository = watchListTickerRepository,
            watchListFactory = mock()
        )
    }

    @Test
    fun `createWatchList uses reverse mapper and persists watch list tickers`() {
        val dbUser = DbUser(id = "user-1", email = "test@test.com", fullName = "Test User")
        val tickerDetailsList = listOf(
            TickerDetails(
                symbol = "AAPL",
                exchange = "NASDAQ",
                tickerName = "Apple Inc.",
                industry = "Technology",
                marketCap = 3_000_000_000_000L,
                price = null
            )
        )
        val tickers = listOf(
            Ticker(tickerCode = "AAPL", tickerLongName = "Apple Inc.", tickerExchange = "NASDAQ", tickerDetails = "{}")
        )
        val savedTickers = tickers

        val serviceUser = ServiceUser(id = "user-1", fullName = "Test User", email = "test@test.com")
        val serviceWatchList = ServiceWatchList(
            id = null, name = "My List", description = "Desc", createdBy = "user-1",
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = null, updatedAt = null, tickerSymbols = listOf("AAPL")
        )
        val dbWatchList = DbWatchList(
            id = null, name = "My List", description = "Desc", createdBy = dbUser,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED
        )
        val savedDbWatchList = DbWatchList(
            id = 1L, name = "My List", description = "Desc", createdBy = dbUser,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED
        )
        val watchListTickers = listOf(
            WatchListTicker(id = null, watchList = savedDbWatchList, tickerCode = "AAPL")
        )

        whenever(tickerMapper.toDbTickers(tickerDetailsList)).thenReturn(tickers)
        whenever(tickerRepository.saveAll(tickers)).thenReturn(savedTickers)
        whenever(watchListMapper.toDbWatchList(serviceWatchList, serviceUser)).thenReturn(dbWatchList)
        whenever(watchListRepository.save(dbWatchList)).thenReturn(savedDbWatchList)
        whenever(watchListMapper.toDbWatchListTickers(savedDbWatchList, listOf("AAPL"))).thenReturn(watchListTickers)
        whenever(watchListMapper.toWatchListServiceModelFromDbWatchListAndTickers(savedDbWatchList, savedTickers))
            .thenReturn(serviceWatchList)

        val result = watchListTxService.createWatchList(serviceUser, serviceWatchList, tickerDetailsList)

        verify(tickerMapper).toDbTickers(tickerDetailsList)
        verify(tickerRepository).saveAll(tickers)
        verify(watchListMapper).toDbWatchList(serviceWatchList, serviceUser)
        verify(watchListRepository).save(dbWatchList)
        verify(watchListMapper).toDbWatchListTickers(savedDbWatchList, listOf("AAPL"))
        verify(watchListTickerRepository).saveAll(watchListTickers)
        verify(watchListMapper).toWatchListServiceModelFromDbWatchListAndTickers(savedDbWatchList, savedTickers)

        assertEquals(serviceWatchList, result)
    }

    @Test
    fun `addTickersToWatchList persists new tickers and returns full ticker list`() {
        val watchListId = 1L
        val dbWatchList = DbWatchList(
            id = watchListId, name = "My List", description = "Desc", createdBy = null,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED
        )
        val tickerDetailsList = listOf(
            TickerDetails(
                symbol = "MSFT", exchange = "NASDAQ", tickerName = "Microsoft Corp.",
                industry = "Technology", marketCap = 2_500_000_000_000L, price = null
            )
        )
        val newTickers = listOf(
            Ticker(tickerCode = "MSFT", tickerLongName = "Microsoft Corp.", tickerExchange = "NASDAQ", tickerDetails = "{}")
        )
        val newWatchListTickers = listOf(
            WatchListTicker(watchList = dbWatchList, tickerCode = "MSFT")
        )
        val allTickers = listOf(
            WatchListTicker(watchList = dbWatchList, tickerCode = "AAPL"),
            WatchListTicker(watchList = dbWatchList, tickerCode = "MSFT")
        )
        val expected = ServiceWatchList(
            id = watchListId, name = "My List", description = "Desc", createdBy = null,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = null, updatedAt = null, tickerSymbols = listOf("AAPL", "MSFT")
        )

        whenever(tickerMapper.toDbTickers(tickerDetailsList)).thenReturn(newTickers)
        whenever(tickerRepository.saveAll(newTickers)).thenReturn(newTickers)
        whenever(watchListRepository.findById(watchListId)).thenReturn(Optional.of(dbWatchList))
        whenever(watchListMapper.toDbWatchListTickers(dbWatchList, listOf("MSFT"))).thenReturn(newWatchListTickers)
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(allTickers)
        whenever(watchListMapper.toWatchListServiceModel(dbWatchList, listOf("AAPL", "MSFT"))).thenReturn(expected)

        val result = watchListTxService.addTickersToWatchList(watchListId, tickerDetailsList)

        verify(tickerMapper).toDbTickers(tickerDetailsList)
        verify(tickerRepository).saveAll(newTickers)
        verify(watchListTickerRepository).saveAll(newWatchListTickers)
        assertEquals(expected, result)
    }

    @Test
    fun `addTickersToWatchList throws NOT_FOUND when watchlist no longer exists`() {
        val tickerDetails = TickerDetails(
            symbol = "MSFT", exchange = "NASDAQ", tickerName = "Microsoft Corp.",
            industry = "Technology", marketCap = 2_500_000_000_000L, price = null
        )
        whenever(watchListRepository.findById(999L)).thenReturn(Optional.empty())

        val exception = assertThrows<WatchListException> {
            watchListTxService.addTickersToWatchList(999L, listOf(tickerDetails))
        }

        assertEquals(WatchListExceptionErrorCode.NOT_FOUND, exception.errorCode)
        verify(tickerRepository, never()).saveAll(any<List<Ticker>>())
        verify(watchListTickerRepository, never()).saveAll(any<List<WatchListTicker>>())
    }

    @Test
    fun `removeTickersFromWatchList deletes tickers and returns remaining ticker list`() {
        val watchListId = 1L
        val dbWatchList = DbWatchList(
            id = watchListId, name = "My List", description = "Desc", createdBy = null,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED
        )
        val remaining = listOf(WatchListTicker(watchList = dbWatchList, tickerCode = "AAPL"))
        val expected = ServiceWatchList(
            id = watchListId, name = "My List", description = "Desc", createdBy = null,
            visibility = WatchListVisibility.PRIVATE, type = WatchListType.FIXED,
            screenQuery = null, createdAt = null, updatedAt = null, tickerSymbols = listOf("AAPL")
        )

        whenever(watchListRepository.findById(watchListId)).thenReturn(Optional.of(dbWatchList))
        whenever(watchListTickerRepository.findTickersByWatchListId(watchListId)).thenReturn(remaining)
        whenever(watchListMapper.toWatchListServiceModel(dbWatchList, listOf("AAPL"))).thenReturn(expected)

        val result = watchListTxService.removeTickersFromWatchList(watchListId, listOf("MSFT"))

        verify(watchListTickerRepository).deleteByWatchListIdAndTickerCodeIn(watchListId, listOf("MSFT"))
        assertEquals(expected, result)
    }

    @Test
    fun `removeTickersFromWatchList throws NOT_FOUND without deleting join rows when watchlist no longer exists`() {
        whenever(watchListRepository.findById(999L)).thenReturn(Optional.empty())

        val exception = assertThrows<WatchListException> {
            watchListTxService.removeTickersFromWatchList(999L, listOf("MSFT"))
        }

        assertEquals(WatchListExceptionErrorCode.NOT_FOUND, exception.errorCode)
        verify(watchListTickerRepository, never()).deleteByWatchListIdAndTickerCodeIn(999L, listOf("MSFT"))
    }
}
