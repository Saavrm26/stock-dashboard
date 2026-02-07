package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.savedrequest.NullRequestCache
import xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.auth.CognitoLogoutHandler

@EnableWebSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    fun jwtDecoder(@Value($$"${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") jwkSetUri: String): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    @Order(1)
    @Bean
    fun apiSecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        val nullRequestCache = NullRequestCache()
        httpSecurity {
            securityMatcher("/api/**")
            requestCache {
                requestCache = nullRequestCache
            }
            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
        return httpSecurity.build()
    }

    @Order(2)
    @Bean
    fun appSecurityFilterChain(
        httpSecurity: HttpSecurity, cognitoLogoutHandler: CognitoLogoutHandler
    ): SecurityFilterChain {
        // TODO: add a logout endpoint
        val nullRequestCache = NullRequestCache()
        httpSecurity {
            securityMatcher("/**")
            requestCache {
                requestCache = nullRequestCache
            }
            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }
            oauth2Login { }
            logout {
                logoutSuccessHandler = cognitoLogoutHandler
            }
            csrf { }
        }
        return httpSecurity.build()
    }

}