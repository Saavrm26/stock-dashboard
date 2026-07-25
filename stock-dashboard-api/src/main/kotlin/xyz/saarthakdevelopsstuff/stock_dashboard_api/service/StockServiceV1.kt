package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerSearchResult

@Service
class StockServiceV1(
    private val tickerRepository: TickerRepository,
    private val tickerMapper: TickerMapper,
    private val stockCacheService: StockCacheService,
    private val stockClient: StockClient
) {
    private val logger = LoggerFactory.getLogger(StockServiceV1::class.java)

    fun getTickerDetails(ticker: String): TickerDetails? {
        return stockClient.getTickerDetails(ticker)?.let { tickerMapper.toTickerDetails(it) }
    }

    fun searchTickers(query: String): TickerSearchResult? {
        return stockClient.searchTickers(query)?.let { tickerMapper.toTickerSearchResult(it) }
    }

    fun getBulkTickerDetails(tickerCodes: List<String>): List<TickerDetails> {
        val cachedTickerDetails = stockCacheService.getBulkTickerDetails(tickerCodes)
        val cachedBySymbol = cachedTickerDetails.associateBy { it.symbol }

        val missingTickers = tickerCodes.distinct().filter { it !in cachedBySymbol }

        val apiTickerDetailsList = stockClient.getBulkTickerDetails(missingTickers)
        stockCacheService.setBulkTickerDetails(apiTickerDetailsList)

        val freshTickerDetails = apiTickerDetailsList.tickersList.map { tickerMapper.toTickerDetails(it) }

        return freshTickerDetails + cachedTickerDetails
    }

}
