package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails
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
    val tickers: List<TickerDetails>? = null
)
