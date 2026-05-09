package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.v1

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.UserDto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.StockDashboardOAuth2UserService
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.UserMapper

@RestController
@RequestMapping("v1/users")
class UserController(
    val stockDashboardOAuth2UserService: StockDashboardOAuth2UserService,
    private val userMapper: UserMapper
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("me")
    fun getCurrentUser(@AuthenticationPrincipal user: OidcUser): UserDto.User {
        logger.info("Fetching the current user ${user.subject}")
        val user = stockDashboardOAuth2UserService.getUser(user.subject)
        return userMapper.toUserDto(user)
    }
}