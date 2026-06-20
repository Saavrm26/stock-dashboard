package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto

import jakarta.validation.constraints.Size

data class AddTickersToWatchListRequest(
    @field:Size(max = 20, message = "Tickers list cannot exceed 20 items")
    val tickers: List<String>
)

data class RemoveTickersFromWatchListRequest(
    val tickers: List<String>
)
