package xyz.saarthakdevelopsstuff.stock_dashboard_api.models

import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails

data class CreateTickerRequest(
    val tickerCode: String,
    val tickerLongName: String,
    val tickerExchange: String,
    val tickerDetails: TickerDetails
)
