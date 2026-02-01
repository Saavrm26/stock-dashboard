package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.savedrequest.NullRequestCache

@EnableWebSecurity
@Configuration
class SecurityConfiguration {
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
        }
        return httpSecurity.build()

    }

    @Order(2)
    @Bean
    fun appSecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
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
            oauth2Login {  }
            csrf {  }
        }
        return httpSecurity.build()
    }

}