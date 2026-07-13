package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse

@Service
class WatchListTxService(
    private val watchListRepository: WatchListRepository,
    private val watchListMapper: WatchListMapper,
    private val tickerRepository: TickerRepository,
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListFactory: WatchListFactory
) {
    @Transactional
    fun createWatchList(
        user: User,
        request: WatchListRequest,
        missingTickers: List<Ticker>
    ): WatchListResponse {
        tickerRepository.saveAll(missingTickers)

        val watchList = watchListFactory.createEmptyUserWatchList(
            watchListName = request.name,
            watchListDescription = request.description,
            user = user,
            visibility = request.visibility
        )
        val savedWatchList = watchListRepository.save(watchList)

        watchListTickerRepository.saveAll(
            request.tickers.distinctBy { it.tickerCode }.map {
                WatchListTicker(
                    watchList = savedWatchList,
                    tickerCode = it.tickerCode
                )
            }
        )

        return watchListMapper.toWatchListResponse(savedWatchList)
    }

    @Transactional
    fun findByIdOrNull(id: Long) : WatchListOuterClass.WatchList? {
        val watchListDb = watchListRepository.findByIdOrNull(id) ?: return null
        return watchListMapper.toWatchListProto(watchListDb)
    }
}
