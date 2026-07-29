package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import jakarta.json.JsonPatch
import jakarta.json.JsonStructure
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
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
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListAggregate
import xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.annotation.ValidWatchListJsonPatch

@Service
@Validated
class WatchListServiceV1(
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListMapper: WatchListMapper,
    private val userRepository: UserRepository,
    private val watchListFactory: WatchListFactory,
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
        val tickerCodes = requestedTickers.distinctBy { it.tickerCode }.map { it.tickerCode }
        val details = stockService.getBulkTickerDetails(tickerCodes)
        val detailsByCode = details.associateBy { it.symbol }
        val unresolved = tickerCodes.filter { it !in detailsByCode }

        if (unresolved.isNotEmpty()) {
            throw StockClientException(
                StockClientErrorCode.DOWNSTREAM_FAILURE,
                "Ticker details were not returned for: ${unresolved.joinToString()}"
            )
        }

        val requestedByCode = requestedTickers.associateBy { it.tickerCode }
        val tickerDetailsList = tickerCodes.map { detailsByCode.getValue(it) }
        val tickers = tickerDetailsList.map { tickerDetails ->
            Ticker(
                tickerCode = tickerDetails.symbol,
                tickerLongName = tickerDetails.tickerName ?: tickerDetails.symbol,
                tickerExchange = tickerDetails.exchange ?: requestedByCode.getValue(tickerDetails.symbol).tickerExchange,
                tickerDetails = objectMapper.writeValueAsString(tickerDetails)
            )
        }
        // TODO: create empty service level watchlist here

        val watchList = watchListTxService.createWatchList(user, watchListRequest, tickers)
        watchListCacheService.saveWatchList(watchList)
        val aggregate = watchListMapper.toWatchListAggregate(watchList, tickerDetailsList)
        return watchListMapper.toWatchListResponse(aggregate)
    }

    fun updateWatchList(id: Long, username: String, @ValidWatchListJsonPatch jsonPatch: JsonPatch) {
        val watchList = getWatchListById(id)

        val createdByUsername = watchList.createdBy
        if (createdByUsername != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }

        val patchedWatchList = applyPatchToWatchListResource(jsonPatch, watchList)

    }

    fun getWatchList(id: Long, getWatchListRequest: GetWatchListRequest): WatchListResponse {
        val watchList = getWatchListById(id)

        val watchListTickers = watchListTickerRepository.findTickersByWatchListId(id)

        val tickerDetails = if (watchListTickers.isNotEmpty()) {
            val tickerCodes = watchListTickers.map { it.tickerCode }
            stockService.getBulkTickerDetails(tickerCodes)
        } else {
            emptyList()
        }

        val aggregate = watchListMapper.toWatchListAggregate(watchList, tickerDetails)
        return watchListMapper.toWatchListResponse(aggregate)
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
        jsonPatch: JsonPatch, watchList: WatchList
    ): WatchList {
        val patched = jsonPatch.apply(objectMapper.convertValue<JsonStructure>(watchList))
        return objectMapper.convertValue<WatchList>(patched)
    }

}
