package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository

@Service
class WatchListTxService(private val watchListRepository: WatchListRepository, private val watchListMapper: WatchListMapper) {
    @Transactional
    fun findByIdOrNull(id: Long) : WatchListOuterClass.WatchList? {
        val watchListDb = watchListRepository.findByIdOrNull(id) ?: return null
        return watchListMapper.toWatchListProto(watchListDb)
    }
}