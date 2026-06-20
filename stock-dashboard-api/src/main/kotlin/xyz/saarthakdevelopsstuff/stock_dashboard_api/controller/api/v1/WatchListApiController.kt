package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.api.v1

import jakarta.json.JsonPatch
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.AddTickersToWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.RemoveTickersFromWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListAggregatedResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.service.WatchListServiceV1

@RestController
@RequestMapping("/api/v1/watchlist")
class WatchListApiController(
    private val watchListServiceV1: WatchListServiceV1,
    private val watchListMapper: WatchListMapper
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
        val createWatchListModel = watchListMapper.toCreateWatchListServiceModel(watchListRequest)
        val watchList = watchListServiceV1.createWatchList(sub, createWatchListModel)
        logger.info("Successfully created watchlist for $sub")
        val watchListResponse = watchListMapper.toWatchListResponse(watchList)
        return ResponseEntity<WatchListResponse>(watchListResponse, HttpStatus.CREATED)

    }

    @PostMapping("/{id}")
    fun getWatchList(@PathVariable("id") id: Long, @RequestBody getWatchListRequest: GetWatchListRequest): WatchListAggregatedResponse {
        logger.info("Getting watchlist for $id")
        val watchListAggregate = watchListServiceV1.getWatchList(id, getWatchListRequest)
        logger.info("Successfully fetched watchlist for $id")
        val response = watchListMapper.toWatchListAggregatedResponse(watchListAggregate)
        return response
    }

    @PatchMapping("/{id}", consumes = ["application/json-patch+json"])
    fun updateWatchList(@PathVariable("id") id: Long, @AuthenticationPrincipal jwt: Jwt, @RequestBody jsonPatch: JsonPatch) : WatchListResponse {
        logger.info("Updating watchlist for $id")
        val updatedWatchList = watchListServiceV1.updateWatchList(id, jwt.subject, jsonPatch)
        logger.info("Successfully updated watchlist for $id")
        val watchListResponse = watchListMapper.toWatchListResponse(updatedWatchList)
        return watchListResponse
    }

    @PatchMapping("/{id}/tickers")
    fun addTickersToWatchList(
        @PathVariable("id") id: Long,
        @RequestBody @Validated request: AddTickersToWatchListRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): WatchListResponse {
        logger.info("Adding tickers to watchlist $id")
        val updatedWatchList = watchListServiceV1.addTickersToWatchList(id, jwt.subject, request.tickers)
        logger.info("Successfully added tickers to watchlist $id")
        return watchListMapper.toWatchListResponse(updatedWatchList)
    }

    @DeleteMapping("/{id}/tickers")
    fun removeTickersFromWatchList(
        @PathVariable("id") id: Long,
        @RequestBody request: RemoveTickersFromWatchListRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): WatchListResponse {
        logger.info("Removing tickers from watchlist $id")
        val updatedWatchList = watchListServiceV1.removeTickersFromWatchList(id, jwt.subject, request.tickers)
        logger.info("Successfully removed tickers from watchlist $id")
        return watchListMapper.toWatchListResponse(updatedWatchList)
    }
}
