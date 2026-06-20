package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service

data class TickerDetails(
    val symbol: String,
    val exchange: String?,
    val tickerName: String?,
    val industry: String?,
    val marketCap: Long?,
    val price: Double?,
    val additionalDetails: AdditionalTickerDetails = AdditionalTickerDetails()
)
