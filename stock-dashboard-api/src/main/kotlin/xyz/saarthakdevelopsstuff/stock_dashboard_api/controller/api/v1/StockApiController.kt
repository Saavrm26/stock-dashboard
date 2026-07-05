package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.api.v1

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerSearch
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

    @GetMapping("/ticker-details", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTickerDetails(@RequestParam query: String): ResponseEntity<TickerDetailsOuterClass.TickerDetails> {
        logger.info("Fetching ticker details for query: $query")
        val tickerDetails = stockServiceV1.getTickerDetails(query)
        return ResponseEntity<TickerDetailsOuterClass.TickerDetails>(tickerDetails, HttpStatus.OK)
    }

    @GetMapping("/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchTickers(@RequestParam query: String): ResponseEntity<TickerSearch.TickerSearchResponse> {
        logger.info("Searching tickers for query: $query")
        val searchResponse = stockServiceV1.searchTickers(query)
        return ResponseEntity<TickerSearch.TickerSearchResponse>(searchResponse, HttpStatus.OK)
    }

    @PostMapping("/ticker-details/bulk", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBulkTickerDetails(@RequestBody tickers: List<String>): ResponseEntity<TickerDetailsOuterClass.TickerDetailsList> {
        logger.info("Fetching ticker details for query: $tickers")
        val tickersResponse = stockServiceV1.getBulkTickerDetails(tickers)
        return ResponseEntity<TickerDetailsOuterClass.TickerDetailsList>(tickersResponse, HttpStatus.OK)
    }
}
