package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter

data class TickerDetails(
    var symbol: String,
    var sector: String?,
    var marketCap: Long?,
    // The mutable map object itself doesn't change so it can be made val
    @get:JsonAnyGetter
    val additionalDetails: MutableMap<String, Any?>,
) {
    @JsonAnySetter
    fun setAdditionalDetails(key: String, value: Any?) {
        additionalDetails[key] = value
    }
}
