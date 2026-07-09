package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

data class GetWatchListRequest(
    val tickerPageSize: Int,
    val tickerPageNumber: Int
)
