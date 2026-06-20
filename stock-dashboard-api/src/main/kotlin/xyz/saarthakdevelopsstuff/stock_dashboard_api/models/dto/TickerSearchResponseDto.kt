package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto

data class TickerSearchResponseDto(
    val quotes: List<TickerQuoteResponse>
)

data class TickerQuoteResponse(
    val exchange: String,
    val shortName: String,
    val quoteType: String,
    val symbol: String,
    val index: String,
    val score: Double,
    val typeDisp: String,
    val longName: String,
    val exchDisp: String,
    val sector: String,
    val sectorDisp: String,
    val industry: String,
    val industryDisp: String,
    val dispSecIndFlag: Boolean,
    val isYahooFinance: Boolean,
    val prevName: String,
    val nameChangeDate: String
)
