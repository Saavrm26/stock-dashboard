package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.CreateTickerRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.StockServiceV1

// This would a admin only controller
@RestController
@RequestMapping("/api/v1/stocks")
class StockController(
    private val stockServiceV1: StockServiceV1
) {
    @GetMapping
    fun getAllTickers(): ResponseEntity<List<Ticker>> {
        val tickers = stockServiceV1.getAllTickers()
        return ResponseEntity<List<Ticker>>(tickers, HttpStatus.OK)
    }

    @PostMapping
    fun addTicker(@RequestBody createTickerRequest: CreateTickerRequest): ResponseEntity<Ticker> {
        val ticker = stockServiceV1.addTicker(createTickerRequest)
        return ResponseEntity<Ticker>(ticker, HttpStatus.CREATED)
    }
}