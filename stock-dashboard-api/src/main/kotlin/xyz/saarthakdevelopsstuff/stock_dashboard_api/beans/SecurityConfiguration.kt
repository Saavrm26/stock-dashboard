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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties.CognitoConfigurationProperties
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties.CorsConfigurationProperties
import xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.auth.CognitoLogoutHandler

// don't need any custom scopes right now. Will just continue with default scopes

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
            cors { }
        }
        return httpSecurity.build()
    }

    @Order(2)
    @Bean
    fun appSecurityFilterChain(
        httpSecurity: HttpSecurity, cognitoLogoutHandler: CognitoLogoutHandler,
        cognitoConfigurationProperties: CognitoConfigurationProperties
    ): SecurityFilterChain {
        val nullRequestCache = NullRequestCache()
        httpSecurity {
            securityMatcher("/**")
            requestCache {
                requestCache = nullRequestCache
            }
            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }
            oauth2Login {
                userInfoEndpoint { }
                defaultSuccessUrl(cognitoConfigurationProperties.defaultSuccessUrl, true)
            }
            logout {
                logoutSuccessHandler = cognitoLogoutHandler
            }
            csrf { }
            cors { }
        }
        return httpSecurity.build()
    }

    @Bean
    fun corsConfigurationSource(corsConfigurationProperties: CorsConfigurationProperties): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = corsConfigurationProperties.allowedOrigins
        // Allow common HTTP methods
        configuration.allowedMethods = listOf("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
        // Allow all headers
        configuration.allowedHeaders = listOf("*")
        // Allow credentials (cookies, authorization headers)
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}