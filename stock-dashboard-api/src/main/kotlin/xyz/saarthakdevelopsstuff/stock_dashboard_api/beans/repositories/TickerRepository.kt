package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker


interface TickerRepository : JpaRepository<Ticker, String> {
    fun save(ticker: Ticker): Ticker
}