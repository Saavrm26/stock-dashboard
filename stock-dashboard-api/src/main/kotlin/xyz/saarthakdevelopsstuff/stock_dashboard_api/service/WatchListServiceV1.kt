package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import jakarta.json.JsonPatch
import jakarta.json.JsonStructure
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.StockDashboardOAuth2UserService
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.CreateWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListAggregate
import xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.annotation.ValidWatchListJsonPatch

@Service
@Validated
class WatchListServiceV1(
    private val stockDashboardOAuth2UserService: StockDashboardOAuth2UserService,
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListMapper: WatchListMapper,
    private val watchListFactory: WatchListFactory,
    private val watchListTxService: WatchListTxService,
    private val watchListCacheService: WatchListCacheService,
    private val stockService: StockServiceV1,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(WatchListServiceV1::class.java)
    // DB Tickers are stale fallback persistence (used when the stock client is down and by the
    // future cache-prewarm process), not the source of truth for what to fetch. Ticker details are
    // always resolved fresh through StockServiceV1, which is Redis-cached.
    fun createWatchList(username: String, createWatchList: CreateWatchList): WatchList {
        val user = getUser(username)
        val tickerCodes = createWatchList.tickers.distinctBy { it.tickerCode }.map { it.tickerCode }
        val detailsByCode = getBulkTickerDetails(tickerCodes)

        val tickerDetailsList = tickerCodes.map { detailsByCode.getValue(it) }

        val serviceWatchList = watchListFactory.createFixedUserWatchList(
            watchListName = createWatchList.name,
            watchListDescription = createWatchList.description,
            user = user,
            visibility = createWatchList.visibility,
            tickerSymbols = tickerCodes
        )
        val watchList = watchListTxService.createWatchList(user, serviceWatchList, tickerDetailsList)
//        watchListCacheService.saveWatchList(watchList)
//        val aggregate = watchListMapper.toWatchListAggregate(watchList, tickerDetailsList)
//        return watchListMapper.toWatchListResponse(aggregate)
        return watchList
    }


    fun updateWatchList(id: Long, username: String, @ValidWatchListJsonPatch jsonPatch: JsonPatch): WatchList {
        val watchList = watchListTxService.findByIdOrNull(id) ?: throw WatchListException(
            WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists"
        )
        val user = getUser(username)
        val createdByUsername = watchList.createdBy
        if (createdByUsername != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }
        val patchedWatchList = getPatchWatchListDetails(jsonPatch, watchList)
        val updatedWatchList =  watchListTxService.updateWatchListDetails(user, patchedWatchList)
        watchListCacheService.evict(id)
        return updatedWatchList
    }

    fun addTickersToWatchList(id: Long, username: String, tickerSymbols: List<String>): WatchList {
        val watchList = watchListTxService.findByIdOrNull(id) ?: throw WatchListException(
            WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists"
        )
        if (watchList.createdBy != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }
        val existingTickerCodes = watchListTickerRepository.findTickersByWatchListId(id).map { it.tickerCode }.toSet()
        val newTickerCodes = tickerSymbols.distinct().filter { it !in existingTickerCodes }
        val detailsByCode = getBulkTickerDetails(newTickerCodes)
        val tickerDetailsList = newTickerCodes.map { detailsByCode.getValue(it) }

        val updatedWatchList = watchListTxService.addTickersToWatchList(id, tickerDetailsList)
        watchListCacheService.evict(id)
        return updatedWatchList
    }

    fun removeTickersFromWatchList(id: Long, username: String, tickerSymbols: List<String>): WatchList {
        val watchList = watchListTxService.findByIdOrNull(id) ?: throw WatchListException(
            WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists"
        )
        if (watchList.createdBy != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }
        val updatedWatchList = watchListTxService.removeTickersFromWatchList(id, tickerSymbols.distinct())
        watchListCacheService.evict(id)
        return updatedWatchList
    }

    fun getWatchList(id: Long, getWatchListRequest: GetWatchListRequest): WatchListAggregate {
        val watchList = getWatchListById(id)

        val watchListTickers = watchListTickerRepository.findTickersByWatchListId(id)

        val tickerDetails = if (watchListTickers.isNotEmpty()) {
            val tickerCodes = watchListTickers.map { it.tickerCode }
            stockService.getBulkTickerDetails(tickerCodes)
        } else {
            emptyList()
        }

        val aggregate = watchListMapper.toWatchListAggregate(watchList, tickerDetails)
        logger.info("Fetch watchlist aggregate with id ${aggregate.id}")
        return aggregate
    }


    private fun getUser(username: String): User =
        stockDashboardOAuth2UserService.getUser(username) ?: throw WatchListException(
            WatchListExceptionErrorCode.USER_NOT_FOUND, "The user creating watchlist couldn't be found. "
        )

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

    private fun getBulkTickerDetails(tickerCodes: List<String>): Map<String, TickerDetails> {
        val details = stockService.getBulkTickerDetails(tickerCodes)
        val detailsByCode = details.associateBy { it.symbol }
        val unresolved = tickerCodes.filter { it !in detailsByCode }

        if (unresolved.isNotEmpty()) {
            throw StockClientException(
                StockClientErrorCode.DOWNSTREAM_FAILURE,
                "Ticker details were not returned for: ${unresolved.joinToString()}"
            )
        }
        return detailsByCode
    }

    private fun getPatchWatchListDetails(
        watchListDetailsJsonPatch: JsonPatch, watchList: WatchList
    ): WatchList {
        val patched = watchListDetailsJsonPatch.apply(objectMapper.convertValue<JsonStructure>(watchList))
        return objectMapper.convertValue<WatchList>(patched)
    }

}
