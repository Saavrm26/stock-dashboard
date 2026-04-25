package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix="cognito")
data class CognitoConfigurationProperties(
    val domain: String,
    val clientId: String,
    val logoutRedirectUrl: String,
    val defaultSuccessUrl: String
)