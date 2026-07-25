package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import jakarta.json.JsonPatch
import jakarta.json.JsonStructure
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.annotation.ValidWatchListJsonPatch

@Service
class WatchListServiceV1(
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListMapper: WatchListMapper,
    private val userRepository: UserRepository,
    private val stockClient: StockClient,
    private val watchListTxService: WatchListTxService,
    private val watchListCacheService: WatchListCacheService,
    private val stockService: StockServiceV1,
    private val objectMapper: ObjectMapper
) {
    // DB Tickers are stale fallback persistence (used when the stock client is down and by the
    // future cache-prewarm process), not the source of truth for what to fetch. Ticker details are
    // always resolved fresh through StockServiceV1, which is Redis-cached.
    fun createWatchList(username: String, watchListRequest: WatchListRequest): WatchListResponse {
        val user = userRepository.findByIdOrNull(username) ?: throw WatchListException(
            WatchListExceptionErrorCode.USER_NOT_FOUND, "The user creating watchlist couldn't be found. "
        )

        val requestedTickers = watchListRequest.tickers
        val details = stockService.getBulkTickerDetails(requestedTickers.map { it.tickerCode })
        val detailsByCode = details.associateBy { it.symbol }
        val unresolved = requestedTickers.map { it.tickerCode }.filter { it !in detailsByCode }

        if (unresolved.isNotEmpty()) {
            throw StockClientException(
                StockClientErrorCode.DOWNSTREAM_FAILURE,
                "Ticker details were not returned for: ${unresolved.joinToString()}"
            )
        }

        val tickers = requestedTickers.distinctBy { it.tickerCode }.map { request ->
            val tickerDetails = detailsByCode.getValue(request.tickerCode)
            Ticker(
                tickerCode = tickerDetails.symbol,
                tickerLongName = tickerDetails.tickerName ?: tickerDetails.symbol,
                tickerExchange = tickerDetails.exchange ?: request.tickerExchange,
                tickerDetails = objectMapper.writeValueAsString(tickerDetails)
            )
        }

        val watchList = watchListTxService.createWatchList(user, watchListRequest, tickers)
        return watchListMapper.toWatchListResponse(watchList)
    }

    fun updateWatchList(id: Long, username: String, @ValidWatchListJsonPatch jsonPatch: JsonPatch) {
        // use cache service to get the watchlist entity itself first
        val watchList = watchListMapper.toWatchListResponse(getWatchListById(id))

        val createdByUsername = watchList.createdBy
        if (createdByUsername != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }

        val patchedWatchListResource = applyPatchToWatchListResource(jsonPatch, watchList)
        if (patchedWatchListResource.id != watchList.id) {
            throw WatchListException(WatchListExceptionErrorCode.BAD_ACTION, "Id cannot be patched")
        }
    }

    fun getWatchList(id: Long, getWatchListRequest: GetWatchListRequest): WatchListResponse {
        // check cache, use fallback, if fallback not present throw else save in cache
        val watchList = getWatchListById(id)

        val watchListTickers = watchListTickerRepository.findTickersByWatchListId(id)

        if (watchListTickers.isNotEmpty()) {
            val tickerCodes = watchListTickers.map { it.tickerCode }
            stockClient.getBulkTickerDetails(tickerCodes)
        }

        return watchListMapper.toWatchListResponse(watchList)
    }

    private fun getWatchListById(id: Long): WatchList {
        var watchList = watchListCacheService.findByIdOrNull(id)
        if (watchList == null) {
            watchList = watchListTxService.findByIdOrNull(id) ?: throw WatchListException(
                WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists"
            )
            watchListCacheService.saveWatchList(watchList)
        }
        return watchList
    }

    private fun applyPatchToWatchListResource(
        jsonPatch: JsonPatch, watchList: WatchListResponse
    ): WatchListResponse {
        val patched = jsonPatch.apply(objectMapper.convertValue<JsonStructure>(watchList))
        return objectMapper.convertValue<WatchListResponse>(patched)
    }

}
