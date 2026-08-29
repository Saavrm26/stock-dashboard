package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListVisibility

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
class WatchListTickerRepositoryTest {

    @Autowired
    lateinit var watchListRepository: WatchListRepository

    @Autowired
    lateinit var watchListTickerRepository: WatchListTickerRepository

    @Autowired
    lateinit var tickerRepository: TickerRepository

    @Test
    fun `findTickersByWatchListId returns tickers for watchlist`() {
        val apple = tickerRepository.save(
            Ticker(
                tickerCode = "AAPL",
                tickerLongName = "Apple Inc.",
                tickerExchange = "NASDAQ",
                tickerDetails = """{"symbol":"AAPL","industry":"Technology","marketCap":3000000000000}"""
            )
        )

        val google = tickerRepository.save(
            Ticker(
                tickerCode = "GOOGL",
                tickerLongName = "Alphabet Inc.",
                tickerExchange = "NASDAQ",
                tickerDetails = """{"symbol":"GOOGL","industry":"Technology","marketCap":2000000000000}"""
            )
        )

        val watchList = WatchList(
            name = "Test WatchList",
            description = "Test Description",
            createdBy = null,
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC
        )
        val savedWatchList = watchListRepository.save(watchList)

        val ticker1 = WatchListTicker(watchList = savedWatchList, tickerCode = apple.tickerCode)
        val ticker2 = WatchListTicker(watchList = savedWatchList, tickerCode = google.tickerCode)
        watchListTickerRepository.saveAll(listOf(ticker1, ticker2))

        val result = watchListTickerRepository.findTickersByWatchListId(savedWatchList.id!!)

        assertEquals(2, result.size)
        assertEquals(setOf("AAPL", "GOOGL"), result.map { it.tickerCode }.toSet())
    }

    @Test
    fun `deleteByWatchListIdAndTickerCodeIn removes only matching tickers`() {
        val apple = tickerRepository.save(
            Ticker(
                tickerCode = "AAPL",
                tickerLongName = "Apple Inc.",
                tickerExchange = "NASDAQ",
                tickerDetails = "{}"
            )
        )
        val google = tickerRepository.save(
            Ticker(
                tickerCode = "GOOGL",
                tickerLongName = "Alphabet Inc.",
                tickerExchange = "NASDAQ",
                tickerDetails = "{}"
            )
        )

        val watchList = WatchList(
            name = "Test WatchList",
            description = "Test Description",
            createdBy = null,
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC
        )
        val savedWatchList = watchListRepository.save(watchList)

        watchListTickerRepository.saveAll(
            listOf(
                WatchListTicker(watchList = savedWatchList, tickerCode = apple.tickerCode),
                WatchListTicker(watchList = savedWatchList, tickerCode = google.tickerCode)
            )
        )

        watchListTickerRepository.deleteByWatchListIdAndTickerCodeIn(savedWatchList.id!!, listOf("AAPL"))

        val remaining = watchListTickerRepository.findTickersByWatchListId(savedWatchList.id!!)
        assertEquals(listOf("GOOGL"), remaining.map { it.tickerCode })
    }
}
