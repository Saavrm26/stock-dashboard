package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service

data class CreateWatchListTicker(
    val tickerCode: String,
    val tickerExchange: String
)

data class CreateWatchList(
    val name: String,
    val description: String,
    val visibility: WatchListVisibility = WatchListVisibility.PRIVATE,
    val type: WatchListType? = null,
    val screenQuery: String? = null,
    val tickers: List<CreateWatchListTicker> = emptyList()
)
