package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cors")
data class CorsConfigurationProperties(
   val allowedOrigins: List<String>
)