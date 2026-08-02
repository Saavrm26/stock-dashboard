package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.api.v1

import jakarta.json.JsonPatch
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.WatchListServiceV1

@RestController
@RequestMapping("/api/v1/watchlist")
class WatchListApiController(
    private val watchListServiceV1: WatchListServiceV1
) {
    private val logger = LoggerFactory.getLogger(WatchListApiController::class.java)

    @PostMapping()
    fun createWatchList(
        @RequestBody @Validated watchListRequest: WatchListRequest,
        @AuthenticationPrincipal jwt: Jwt,
        authentication: Authentication
    ) : ResponseEntity<WatchListResponse> {
        val sub = jwt.subject
        logger.info("Creating watchlist for $sub")
        val watchListResponse = watchListServiceV1.createWatchList(sub, watchListRequest)
        logger.info("Successfully created watchlist for $sub")
        return ResponseEntity<WatchListResponse>(watchListResponse, HttpStatus.OK)

    }

    @PostMapping("/{id}")
    fun getWatchList(@PathVariable("id") id: Long, @RequestBody getWatchListRequest: GetWatchListRequest): WatchListResponse {
        logger.info("Getting watchlist for $id")
        return watchListServiceV1.getWatchList(id, getWatchListRequest)
    }

    @PatchMapping("/{id}", consumes = ["application/json-patch+json"])
    fun updateWatchList(@PathVariable("id") id: Long, @AuthenticationPrincipal jwt: Jwt, @RequestBody jsonPatch: JsonPatch) {
        logger.info("Updating watchlist for $id")
        watchListServiceV1.updateWatchList(id, jwt.subject, jsonPatch)
    }
}