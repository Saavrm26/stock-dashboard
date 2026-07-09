package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

import jakarta.validation.constraints.Size
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails

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
