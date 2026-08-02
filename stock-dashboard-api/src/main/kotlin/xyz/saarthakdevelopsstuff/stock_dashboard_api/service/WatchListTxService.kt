package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListRequest
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User as ServiceUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList

@Service
class WatchListTxService(
    private val watchListRepository: WatchListRepository,
    private val watchListMapper: WatchListMapper,
    private val tickerRepository: TickerRepository,
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListFactory: WatchListFactory
) {
    private val logger = LoggerFactory.getLogger(WatchListTxService::class.java)
    @Transactional
    fun createWatchList(
        user: ServiceUser,
        serviceWatchList: WatchList,
        tickers: List<Ticker>
    ): WatchList {
        val savedTickers = tickerRepository.saveAll(tickers)


        val dbWatchList = watchListMapper.toDbWatchList(serviceWatchList, user)
        val savedDbWatchList = watchListRepository.save(dbWatchList)
        val tickerSymbols = serviceWatchList.tickerSymbols
        if (tickerSymbols != null) {
            val tickerEntities = watchListMapper.toDbWatchListTickers(savedDbWatchList, tickerSymbols)
            val savedWatchListTickers = watchListTickerRepository.saveAll(tickerEntities)
            logger.info("Saved the tickers $savedWatchListTickers")
        }

        return watchListMapper.toWatchListServiceModelFromDbWatchListAndTickers(savedDbWatchList, tickers = savedTickers)
    }

    @Transactional
    fun findByIdOrNull(id: Long): WatchList? {
        val dbWatchList = watchListRepository.findByIdOrNull(id) ?: return null
        val dbWatchListTickerSymbols = watchListTickerRepository.findTickersByWatchListId(id).map { it.tickerCode }
        return  watchListMapper.toWatchListServiceModel(dbWatchList, dbWatchListTickerSymbols)
    }
}
