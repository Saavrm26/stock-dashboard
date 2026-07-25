package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto

import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.AdditionalTickerDetails

data class TickerDetailsResponse(
    val symbol: String,
    val exchange: String?,
    val tickerName: String?,
    val industry: String?,
    val marketCap: Long?,
    val price: Double?,
    val additionalDetails: AdditionalTickerDetails = AdditionalTickerDetails()
)

data class TickerDetailsListResponse(
    val tickers: List<TickerDetailsResponse>
)
