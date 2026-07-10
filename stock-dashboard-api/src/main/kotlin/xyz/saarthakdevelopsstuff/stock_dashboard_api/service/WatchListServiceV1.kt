package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.CachedWatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.GetWatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse

@Service
class WatchListServiceV1(
    private val watchListRepository: WatchListRepository,
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListMapper: WatchListMapper,
    private val userRepository: UserRepository,
    private val watchListFactory: WatchListFactory,
    private val stockClient: StockClient,
    private val watchListTxService: WatchListTxService,
    private val watchListCacheService: WatchListCacheService
) {
    @Transactional
    fun createWatchList(username: String, watchListRequest: WatchListRequest): WatchListResponse {
        val user = userRepository.findByIdOrNull(username) ?: throw WatchListException(
            WatchListExceptionErrorCode.USER_NOT_FOUND,
            "The user creating watchlist couldn't be found. "
        )
        val watchList = watchListFactory.createEmptyUserWatchList(
            watchListName = watchListRequest.name,
            watchListDescription = watchListRequest.description,
            user = user,
            visibility = watchListRequest.visibility
        )
        val savedWatchList = watchListRepository.save(watchList)

        watchListRequest.tickers.forEach { tickerRequest ->
            val ticker = WatchListTicker(
                watchList = savedWatchList,
                tickerCode = tickerRequest.tickerCode,
                tickerLongName = "",
                tickerExchange = tickerRequest.tickerExchange,
                tickerDetails = TickerDetails(
                    symbol = tickerRequest.tickerCode,
                    sector = null,
                    marketCap = null,
                    additionalDetails = mutableMapOf()
                )
            )
            watchListTickerRepository.save(ticker)
        }

        return watchListMapper.toWatchListResponse(savedWatchList)
    }

    @Transactional
    fun updateWatchList(id: Long, username: String) {
        val createdByUsername = watchListRepository.findCreatedByByWatchListId(id) ?: throw WatchListException(
            WatchListExceptionErrorCode.NOT_FOUND,
            "Watch List not found!"
        )
        if (createdByUsername != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }
    }

    fun getWatchList(id: Long, getWatchListRequest: GetWatchListRequest): WatchListResponse {
        // check cache, use fallback, if fallback not present throw else save in cache
        var watchList = watchListCacheService.findByIdOrNull(id)
        if (watchList == null) {
            watchList = watchListTxService.findByIdOrNull(id) ?: throw WatchListException(
                WatchListExceptionErrorCode.NOT_FOUND,
                "This watch list doesn't exists"
            )
            watchListCacheService.saveWatchList(watchList)
        }

        val watchListTickers = watchListTickerRepository.findTickersByWatchListId(id)

        if (watchListTickers.isNotEmpty()) {
            val tickerCodes = watchListTickers.map { it.tickerCode }
            stockClient.getBulkTickerDetails(tickerCodes)
        }

        return watchListMapper.toWatchListResponse(watchList)
    }
}