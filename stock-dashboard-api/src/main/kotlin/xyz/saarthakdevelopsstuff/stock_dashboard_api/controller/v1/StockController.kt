package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.CreateTickerRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.StockServiceV1

@RestController
@RequestMapping("/api/v1/stocks")
class StockController(
    private val stockServiceV1: StockServiceV1
) {
    @GetMapping
    fun getAllTickers(): List<String> {
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication

        println(authentication)
        println(authentication.name)
        println(authentication.principal)
        println(authentication.authorities)
        return listOf("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA")
    }

    @PostMapping
    fun addTicker(@RequestBody createTickerRequest: CreateTickerRequest): ResponseEntity<Ticker> {
        val ticker = stockServiceV1.addTicker(createTickerRequest)
        return ResponseEntity<Ticker>(ticker, HttpStatus.CREATED)
    }
}