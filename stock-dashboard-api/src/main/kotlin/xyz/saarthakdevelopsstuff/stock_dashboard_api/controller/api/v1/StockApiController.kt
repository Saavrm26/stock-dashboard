package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.api.v1

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerSearchResponseDto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.StockServiceV1

// This would a admin only controller
@RestController
@RequestMapping("/api/v1/stocks")
class StockApiController(
    private val stockServiceV1: StockServiceV1,
    private val tickerMapper: TickerMapper
) {
    private val logger = LoggerFactory.getLogger(StockApiController::class.java)

    @GetMapping("/ticker-details", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTickerDetails(@RequestParam query: String): ResponseEntity<TickerDetailsResponse> {
        logger.info("Fetching ticker details for query: $query")
        val tickerDetails = stockServiceV1.getTickerDetails(query)
        return ResponseEntity(tickerDetails?.let { tickerMapper.toTickerDetailsResponse(it) }, HttpStatus.OK)
    }

    @GetMapping("/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchTickers(@RequestParam query: String): ResponseEntity<TickerSearchResponseDto> {
        logger.info("Searching tickers for query: $query")
        val searchResponse = stockServiceV1.searchTickers(query)
        return ResponseEntity(searchResponse?.let { tickerMapper.toTickerSearchResponseDto(it) }, HttpStatus.OK)
    }

    @PostMapping("/ticker-details/bulk", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBulkTickerDetails(@RequestBody tickers: List<String>): ResponseEntity<TickerDetailsListResponse> {
        logger.info("Fetching bulk ticker details for query: $tickers")
        val tickersResponse = stockServiceV1.getBulkTickerDetails(tickers)
        return ResponseEntity(tickerMapper.toTickerDetailsListResponse(tickersResponse), HttpStatus.OK)
    }
}
