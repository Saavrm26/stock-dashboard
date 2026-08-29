package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service

data class TickerSearchResult(
    val quotes: List<TickerQuote>
)

data class TickerQuote(
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
