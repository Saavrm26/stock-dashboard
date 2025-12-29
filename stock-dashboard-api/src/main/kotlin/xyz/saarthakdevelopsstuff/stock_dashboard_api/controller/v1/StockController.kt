package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/stocks")
class StockController {
    @GetMapping
    fun getAllStocks() : List<String> {
        return listOf("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA")
    }
}