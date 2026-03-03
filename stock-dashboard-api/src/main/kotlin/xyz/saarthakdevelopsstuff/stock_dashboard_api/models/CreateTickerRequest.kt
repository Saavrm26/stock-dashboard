package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

data class CreateTickerRequest(
    val tickerCode: String,
    val tickerLongName: String,
    val tickerExchange: String,
    val tickerMetadata: TickerMetadata?
)
