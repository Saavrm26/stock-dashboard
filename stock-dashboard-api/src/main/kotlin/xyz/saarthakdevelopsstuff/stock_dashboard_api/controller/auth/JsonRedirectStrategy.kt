package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.RedirectStrategy
import java.io.IOException

class JsonRedirectStrategy : RedirectStrategy {

    private val objectMapper = ObjectMapper()

    @Throws(IOException::class)
    override fun sendRedirect(
        request: HttpServletRequest,
        response: HttpServletResponse,
        url: String
    ) {
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"

        // Write the redirect URL to the response body instead of sending a 302
        val responseBody = mapOf("redirectUrl" to url)
        response.writer.write(objectMapper.writeValueAsString(responseBody))
    }
}