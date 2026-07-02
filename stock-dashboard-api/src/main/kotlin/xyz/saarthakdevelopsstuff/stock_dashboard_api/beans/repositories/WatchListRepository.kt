package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList

interface WatchListRepository : JpaRepository<WatchList, Long> {
    @Query(
        "SELECT created_by FROM WatchList WHERE id = :id",
        nativeQuery = true
    )
    fun findCreatedByByWatchListId(id: Long): String?
}