package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties.CognitoConfigurationProperties
import java.net.URI

@Component
class CognitoLogoutHandler(
    val cognitoConfigurationProperties: CognitoConfigurationProperties
) : SimpleUrlLogoutSuccessHandler() {
    override fun determineTargetUrl(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ): String? {
        return UriComponentsBuilder
            .fromUri(
                URI.create(
                    "${cognitoConfigurationProperties.domain}/logout"
                )
            )
            .queryParam("client_id", cognitoConfigurationProperties.clientId)
            .queryParam("logout_uri", cognitoConfigurationProperties.logoutRedirectUrl).build()
            .toString()
    }
}