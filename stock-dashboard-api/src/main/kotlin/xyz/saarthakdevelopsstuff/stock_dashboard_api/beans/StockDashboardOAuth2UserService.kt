package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service

@Service
class StockDashboardOAuth2UserService : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val user = super.loadUser(userRequest)
        println(user)
        val claims = user.claims
        val email = claims["email"]
        val userName = claims["username"]
        val name = claims["name"]
        // TODO: check if user exists in DB
        // if not, create a new user
        return user
    }

}