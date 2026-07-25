package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker

interface WatchListTickerRepository : JpaRepository<WatchListTicker, Long> {

    @Query("SELECT wlt FROM WatchListTicker wlt WHERE wlt.watchList.id = :watchListId")
    fun findTickersByWatchListId(watchListId: Long): List<WatchListTicker>
}
