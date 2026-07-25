package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerSearchResponseDto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.StockServiceV1

@RestController
@RequestMapping("v1/stocks")
class StockController(
    private val stockServiceV1: StockServiceV1,
    private val tickerMapper: TickerMapper
) {
    private val logger = LoggerFactory.getLogger(StockController::class.java)

    @GetMapping("/ticker-details", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTickerDetails(
        @RequestParam query: String,
        @AuthenticationPrincipal oidcUser: OidcUser
    ): ResponseEntity<TickerDetailsResponse> {
        logger.info("Fetching ticker details for query: $query, user: $oidcUser")
        val tickerDetails = stockServiceV1.getTickerDetails(query)
        return ResponseEntity(tickerDetails?.let { tickerMapper.toTickerDetailsResponse(it) }, HttpStatus.OK)
    }

    @GetMapping("/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchTickers(
        @RequestParam query: String,
        @AuthenticationPrincipal oidcUser: OidcUser
    ): ResponseEntity<TickerSearchResponseDto> {
        logger.info("Searching tickers for query: $query, user: $oidcUser")
        val searchResponse = stockServiceV1.searchTickers(query)
        return ResponseEntity(searchResponse?.let { tickerMapper.toTickerSearchResponseDto(it) }, HttpStatus.OK)
    }

    @PostMapping("/ticker-details/bulk", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBulkTickerDetails(@RequestBody tickers: List<String>): ResponseEntity<TickerDetailsListResponse> {
        logger.info("Fetching ticker details for query: $tickers")
        val tickersResponse = stockServiceV1.getBulkTickerDetails(tickers)
        return ResponseEntity(tickerMapper.toTickerDetailsListResponse(tickersResponse), HttpStatus.OK)
    }

}
