package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.api.v1

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.CreateTickerRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.StockServiceV1

// This would a admin only controller
@RestController
@RequestMapping("/api/v1/stocks")
class StockApiController(
    private val stockServiceV1: StockServiceV1
) {
    private val logger = LoggerFactory.getLogger(StockApiController::class.java)
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

    @GetMapping("/ticker-details", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTickerDetails(@RequestParam query: String): ResponseEntity<TickerDetailsOuterClass.TickerDetails> {
        logger.info("Fetching ticker details for query: $query")
        val tickerDetails = stockServiceV1.getTickerDetails(query)
        return ResponseEntity<TickerDetailsOuterClass.TickerDetails>(tickerDetails, HttpStatus.OK)
    }
}