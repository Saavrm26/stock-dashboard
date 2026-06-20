package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service

import java.time.Instant

data class WatchList(
    val id: Long?,
    val name: String,
    val description: String,
    val createdBy: String?,
    val visibility: WatchListVisibility,
    val type: WatchListType,
    val screenQuery: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val tickerSymbols: List<String>? = null
)

enum class WatchListVisibility { PRIVATE, PUBLIC }

enum class WatchListType { FIXED, DYNAMIC }
