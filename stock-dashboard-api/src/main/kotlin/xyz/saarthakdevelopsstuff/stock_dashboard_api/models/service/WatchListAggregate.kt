package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service

import java.time.Instant

data class WatchListAggregate(
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
