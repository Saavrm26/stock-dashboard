package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
class TickerRepositoryTest {

    @Autowired
    lateinit var tickerRepository: TickerRepository

    @Test
    fun `saves and loads ticker details as JSONB`() {
        val tickerJson = """{"symbol":"AAPL","sector":"Technology","marketCap":3000000000000}"""
        val ticker = Ticker(
            tickerCode = "AAPL",
            tickerLongName = "Apple Inc.",
            tickerExchange = "NASDAQ",
            tickerDetails = tickerJson
        )

        tickerRepository.saveAndFlush(ticker)

        val loaded = tickerRepository.findById("AAPL").get()

        assertEquals("AAPL", loaded.tickerCode)
        assertEquals(tickerJson, loaded.tickerDetails)
    }
}
