package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails as ProtoTickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients.StockClient
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientException
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
    private val stockClient: StockClient,
    private val watchListTxService: WatchListTxService,
    private val watchListCacheService: WatchListCacheService,
    private val tickerRepository: TickerRepository,
    private val tickerMapper: TickerMapper
) {
    // Non functional: Issues: No caching right now
    // Functional: only missing tickers is getting persisted
    // Database operations are spilling out of transactional service
    fun createWatchList(username: String, watchListRequest: WatchListRequest): WatchListResponse {
        val user = userRepository.findByIdOrNull(username) ?: throw WatchListException(
            WatchListExceptionErrorCode.USER_NOT_FOUND,
            "The user creating watchlist couldn't be found. "
        )

        val requestedByCode = watchListRequest.tickers.associateBy { it.tickerCode }
        val existingCodes = tickerRepository.findAllById(requestedByCode.keys)
            .map { it.tickerCode }
            .toSet()

        val missingRequests = requestedByCode
            .filterKeys { it !in existingCodes }
            .values
            .toList()

        val missingTickers = if (missingRequests.isEmpty()) {
            emptyList()
        } else {
            val details = stockClient.getBulkTickerDetails(missingRequests.map { it.tickerCode })
            val detailsByCode = details.tickersList.associateBy { it.symbol }
            val unresolved = missingRequests.map { it.tickerCode }.filter { it !in detailsByCode }

            if (unresolved.isNotEmpty()) {
                throw StockClientException(
                    StockClientErrorCode.DOWNSTREAM_FAILURE,
                    "Ticker details were not returned for: ${unresolved.joinToString()}"
                )
            }

            missingRequests.map { request ->
                val response = detailsByCode.getValue(request.tickerCode)
                Ticker(
                    tickerCode = response.symbol,
                    tickerLongName = if (response.hasShortName()) response.shortName else response.symbol,
                    tickerExchange = if (response.hasExchange()) response.exchange else request.tickerExchange,
                    tickerDetails = tickerMapper.mapTickerDetailsProtoToString(response)
                )
            }
        }

        return watchListTxService.createWatchList(user, watchListRequest, missingTickers)
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
