package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User as DbUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequestTickers
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User as ServiceUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList as ServiceWatchList
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class WatchListTxServiceTest {

    @Mock
    private lateinit var watchListRepository: WatchListRepository

    @Mock
    private lateinit var watchListMapper: WatchListMapper

    @Mock
    private lateinit var tickerRepository: TickerRepository

    @Mock
    private lateinit var watchListTickerRepository: WatchListTickerRepository

    @Mock
    private lateinit var watchListFactory: WatchListFactory

    @InjectMocks
    private lateinit var watchListTxService: WatchListTxService

    @Test
    fun `createWatchList uses factory and reverse mapper`() {
        val dbUser = DbUser(id = "user-1", email = "test@test.com", fullName = "Test User")
        val request = WatchListRequest(
            name = "My List",
            description = "Desc",
            visibility = WatchListVisibility.PRIVATE,
            tickers = listOf(
                WatchListRequestTickers(tickerCode = "AAPL", tickerExchange = "NASDAQ")
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
            screenQuery = null, createdAt = null, updatedAt = null, tickerSymbols = null
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

        whenever(tickerRepository.saveAll(tickers)).thenReturn(savedTickers)
        whenever(watchListFactory.createEmptyUserWatchList("My List", "Desc", serviceUser, WatchListVisibility.PRIVATE))
            .thenReturn(serviceWatchList)
        whenever(watchListMapper.toDbWatchList(serviceWatchList, dbUser)).thenReturn(dbWatchList)
        whenever(watchListRepository.save(dbWatchList)).thenReturn(savedDbWatchList)
        whenever(watchListMapper.toDbWatchListTickers(savedDbWatchList, listOf("AAPL"))).thenReturn(watchListTickers)
        whenever(watchListMapper.toWatchListServiceModelFromDbWatchListAndTickers(savedDbWatchList, savedTickers))
            .thenReturn(serviceWatchList)

        val result = watchListTxService.createWatchList(dbUser, request, tickers)

        verify(tickerRepository).saveAll(tickers)
        verify(watchListFactory).createEmptyUserWatchList("My List", "Desc", serviceUser, WatchListVisibility.PRIVATE)
        verify(watchListMapper).toDbWatchList(serviceWatchList, dbUser)
        verify(watchListRepository).save(dbWatchList)
        verify(watchListMapper).toDbWatchListTickers(savedDbWatchList, listOf("AAPL"))
        verify(watchListTickerRepository).saveAll(watchListTickers)
        verify(watchListMapper).toWatchListServiceModelFromDbWatchListAndTickers(savedDbWatchList, savedTickers)

        assertEquals(serviceWatchList, result)
    }
}
