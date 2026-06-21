package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.UserFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User

@Service
class StockDashboardOAuth2UserService(
    private val oidcUserService: OidcUserService,
    private val userRepository: UserRepository,
    private val userFactory: UserFactory,
    private val watchListFactory: WatchListFactory,
    private val watchListRepository: WatchListRepository
) : OAuth2UserService<OidcUserRequest, OidcUser> {

    private val logger = LoggerFactory.getLogger(StockDashboardOAuth2UserService::class.java)


    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val userDto = oidcUserService.loadUser(userRequest)
        val claims = userDto.claims
        val email = claims["email"] as String
        val userName = claims["username"] as String
        val fullName = claims["name"] as String
        val user = userRepository.findById(userName)
        if (user.isEmpty) {
            saveStandardUser(fullName, userName, email)
        }
        return userDto
    }

    @Transactional
    private fun saveStandardUser(fullName: String, userName: String, email: String) {
        val user = userRepository.save(userFactory.createUser(fullName, userName, email))
        logger.info("Created new user ${user.id}")
        watchListRepository.save(
            watchListFactory.createEmptyUserWatchList(
                watchListName = "My watch list", watchListDescription = "My followed stocks", user = user
            )
        )
    }


    fun getUser(userName: String): User? {
        return userRepository.findById(userName)
    }

}