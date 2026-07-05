package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.api.v1

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.WatchListServiceV1

@RestController
@RequestMapping("/api/v1/watchlist")
class WatchListApiController (
    private val watchListServiceV1: WatchListServiceV1
) {
    private val logger = LoggerFactory.getLogger(WatchListApiController::class.java)

    @PostMapping()
    fun createWatchList(@AuthenticationPrincipal jwt: Jwt, authentication: Authentication) {
        val sub = jwt.subject
        val authorities = authentication.authorities
        logger.info("Creating watchlist for $sub")
    }

    @GetMapping("/{id}")
    fun getWatchList(@PathVariable("id") id: Long) : WatchListResponse {
        logger.info("Getting watchlist for $id")
        return  watchListServiceV1.getWatchList(id)
    }

    @PutMapping("/{id}")
    fun updateWatchList(@PathVariable("id") id: Long, @AuthenticationPrincipal jwt: Jwt) {
        logger.info("Updating watchlist for $id")
    }
}