package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList

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
        // TODO: instead of WatchListRequest take in service layer model
        request: WatchListRequest,
        tickers: List<Ticker>
    ): WatchList {
        val savedTickers = tickerRepository.saveAll(tickers)

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

        return watchListMapper.toWatchListServiceModelFromDbWatchListAndTickers(savedWatchList, tickers = savedTickers)
    }

    @Transactional
    fun findByIdOrNull(id: Long): WatchList? {
        val dbWatchList = watchListRepository.findByIdOrNull(id) ?: return null
        val dbWatchListTickerSymbols = watchListTickerRepository.findTickersByWatchListId(id).map { it.tickerCode }
        return  watchListMapper.toWatchListServiceModel(dbWatchList, dbWatchListTickerSymbols)
    }
}
