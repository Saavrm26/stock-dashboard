package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.StockServiceV1

@RestController
@RequestMapping("v1/stocks")
class StockController(
    private val stockServiceV1: StockServiceV1
) {
    private val logger = LoggerFactory.getLogger(StockController::class.java)

    @GetMapping("/ticker-details", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTickerDetails(@RequestParam query: String, @AuthenticationPrincipal oidcUser: OidcUser) : ResponseEntity<TickerDetailsOuterClass.TickerDetails> {
        logger.info("Fetching ticker details for query: $query, user: $oidcUser")
        val tickerDetails = stockServiceV1.getTickerDetails(query)
        return ResponseEntity<TickerDetailsOuterClass.TickerDetails>(tickerDetails, HttpStatus.OK)
    }

}