package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto

data class GetWatchListRequest(
    val tickerPageSize: Int,
    val tickerPageNumber: Int = 0
)
