package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/stocks")
class StockController {
    @GetMapping
    fun getAllTickers() : List<String> {
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication
        
        println(authentication)
        println(authentication.name)
        println(authentication.principal)
        println(authentication.authorities)
        return listOf("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA")
    }

    @PostMapping
    fun addTicker() {

    }
}