package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto

import jakarta.validation.constraints.Size
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility

data class WatchListRequestTickers(
    val tickerCode: String,
    val tickerExchange: String,
)

data class WatchListRequest(
    val name: String,
    val description: String,
    val visibility: WatchListVisibility = WatchListVisibility.PRIVATE,
    val type: WatchListType? = null,
    val screenQuery: String? = null,
    @field:Size(max = 20, message = "Tickers list cannot exceed 20 items")
    val tickers: List<WatchListRequestTickers> = emptyList(),
)
