package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "clients.stock")
data class StockClientProperties(
    val url: String = ""
)
