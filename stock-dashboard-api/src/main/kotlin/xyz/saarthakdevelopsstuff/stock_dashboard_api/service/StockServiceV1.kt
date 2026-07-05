package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerSearch.TickerSearchResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.CreateTickerRequest

@Service
class StockServiceV1(
    private val tickerRepository: TickerRepository,
    private val tickerMapper: TickerMapper,
    private val stockClient: StockClient
) {
    private val logger = LoggerFactory.getLogger(StockServiceV1::class.java)

    fun getTickerDetails(ticker: String): TickerDetails? {
        return stockClient.getTickerDetails(ticker)
    }

    fun searchTickers(query: String): TickerSearchResponse? {
        return stockClient.searchTickers(query)
    }

    fun getBulkTickerDetails(ticker: List<String>): List<TickerDetails> {
        return listOf()
    }

}