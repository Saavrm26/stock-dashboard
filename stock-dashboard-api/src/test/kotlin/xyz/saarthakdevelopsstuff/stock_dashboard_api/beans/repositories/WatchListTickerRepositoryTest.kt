package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails
import java.time.Instant

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
class WatchListTickerRepositoryTest {

    @Autowired
    lateinit var watchListRepository: WatchListRepository

    @Autowired
    lateinit var watchListTickerRepository: WatchListTickerRepository

    @Test
    fun `findTickersByWatchListId returns tickers for watchlist`() {
        val watchList = WatchList(
            id = null,
            name = "Test WatchList",
            description = "Test Description",
            createdBy = null,
            visibility = WatchListVisibility.PUBLIC,
            type = WatchListType.DYNAMIC,
            screenQuery = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val savedWatchList = watchListRepository.save(watchList)

        val ticker1 = WatchListTicker(
            id = null,
            watchList = savedWatchList,
            tickerCode = "AAPL",
            tickerLongName = "Apple Inc.",
            tickerExchange = "NASDAQ",
            tickerDetails = TickerDetails(symbol = "AAPL", sector = "Technology", marketCap = 3000000000000L),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val ticker2 = WatchListTicker(
            id = null,
            watchList = savedWatchList,
            tickerCode = "GOOGL",
            tickerLongName = "Alphabet Inc.",
            tickerExchange = "NASDAQ",
            tickerDetails = TickerDetails(symbol = "GOOGL", sector = "Technology", marketCap = 2000000000000L),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        watchListTickerRepository.saveAll(listOf(ticker1, ticker2))

        val result = watchListTickerRepository.findTickersByWatchListId(savedWatchList.id!!)

        assertEquals(2, result.size)
        assertEquals(setOf("AAPL", "GOOGL"), result.map { it.tickerCode }.toSet())
    }
}
