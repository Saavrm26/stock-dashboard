package xyz.saarthakdevelopsstuff.stock_dashboard_api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class StockDashboardApiApplicationTests {

    // This prevents Spring from trying to validate the Cognito Issuer URI 
    // by replacing the real registration logic with a mock.
    @MockBean
    lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Test
    fun contextLoads() {
        // The context should now start successfully
    }
}