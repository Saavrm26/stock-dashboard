package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList

interface WatchListRepository : JpaRepository<WatchList, Long> {
    fun save(watchList: WatchList)
}