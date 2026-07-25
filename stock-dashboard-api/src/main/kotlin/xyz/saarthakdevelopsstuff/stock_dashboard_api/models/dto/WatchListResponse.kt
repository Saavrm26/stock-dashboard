package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto

import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import java.time.Instant

data class WatchListResponse(
    val id: Long?,
    val name: String,
    val description: String,
    val createdBy: String?,
    val visibility: WatchListVisibility,
    val type: WatchListType,
    val screenQuery: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val tickers: List<TickerDetailsResponse>? = null
)
