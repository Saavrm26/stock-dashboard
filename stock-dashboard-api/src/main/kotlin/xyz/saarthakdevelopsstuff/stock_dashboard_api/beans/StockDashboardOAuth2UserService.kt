package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User

@Service
class StockDashboardOAuth2UserService(
    private val userRepository: UserRepository
) : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val userDto = super.loadUser(userRequest)
        val claims = userDto.claims
        val email = claims["email"] as String
        val userName = claims["username"] as String
        val fullName = claims["name"] as String
        val user = userRepository.findById(userName)
        if (user == null) {
            userRepository.save(User(userName, email, fullName))
        }
        return userDto
    }

}