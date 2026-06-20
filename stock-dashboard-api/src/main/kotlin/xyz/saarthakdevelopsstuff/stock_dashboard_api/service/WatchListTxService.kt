package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.TickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListTickerRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User as ServiceUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList

@Service
class WatchListTxService(
    private val watchListRepository: WatchListRepository,
    private val watchListMapper: WatchListMapper,
    private val tickerMapper: TickerMapper,
    private val tickerRepository: TickerRepository,
    private val watchListTickerRepository: WatchListTickerRepository,
    private val watchListFactory: WatchListFactory
) {
    private val logger = LoggerFactory.getLogger(WatchListTxService::class.java)
    @Transactional
    fun createWatchList(
        user: ServiceUser,
        serviceWatchList: WatchList,
        tickerDetailsList: List<TickerDetails>
    ): WatchList {
        val tickers = tickerMapper.toDbTickers(tickerDetailsList)
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

    @Transactional
    fun updateWatchListDetails(
        user: ServiceUser,
        patchedServiceWatchList: WatchList,
    ) : WatchList {
        val dbWatchList = watchListMapper.toDbWatchList(patchedServiceWatchList, user)
        val savedDbWatchList = watchListRepository.save(dbWatchList)
        return watchListMapper.toWatchListServiceModel(savedDbWatchList, patchedServiceWatchList.tickerSymbols!!)
    }

    @Transactional
    fun addTickersToWatchList(watchListId: Long, tickerDetailsList: List<TickerDetails>): WatchList {
        val dbWatchList = watchListRepository.findByIdOrNull(watchListId) ?: throw WatchListException(
            WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists"
        )

        val newTickers = tickerMapper.toDbTickers(tickerDetailsList)
        tickerRepository.saveAll(newTickers)

        if (newTickers.isNotEmpty()) {
            val tickerEntities = watchListMapper.toDbWatchListTickers(dbWatchList, newTickers.map { it.tickerCode })
            watchListTickerRepository.saveAll(tickerEntities)
        }

        val allTickerSymbols = watchListTickerRepository.findTickersByWatchListId(watchListId).map { it.tickerCode }
        return watchListMapper.toWatchListServiceModel(dbWatchList, allTickerSymbols)
    }

    @Transactional
    fun removeTickersFromWatchList(watchListId: Long, tickerCodes: List<String>): WatchList {
        val dbWatchList = watchListRepository.findByIdOrNull(watchListId) ?: throw WatchListException(
            WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists"
        )

        watchListTickerRepository.deleteByWatchListIdAndTickerCodeIn(watchListId, tickerCodes)
        val remainingTickerSymbols = watchListTickerRepository.findTickersByWatchListId(watchListId).map { it.tickerCode }
        return watchListMapper.toWatchListServiceModel(dbWatchList, remainingTickerSymbols)
    }
}
