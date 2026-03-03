package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.repository.Repository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker


interface TickerRepository : Repository<Ticker, String> {
    fun save(ticker: Ticker): Ticker
}