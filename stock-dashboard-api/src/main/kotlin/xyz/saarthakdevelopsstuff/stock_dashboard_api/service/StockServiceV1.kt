package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails
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
    fun addTicker(createTickerRequest: CreateTickerRequest): Ticker {
        val ticker = tickerMapper.createTickerRequestToTicker(createTickerRequest)
        return tickerRepository.save(ticker)
    }

    fun getAllTickers(): List<Ticker> {
        return tickerRepository.findAll()
    }

    fun searchTicker(ticker: String): TickerDetails? {
        return stockClient.getTickerDetails(ticker)
    }
}