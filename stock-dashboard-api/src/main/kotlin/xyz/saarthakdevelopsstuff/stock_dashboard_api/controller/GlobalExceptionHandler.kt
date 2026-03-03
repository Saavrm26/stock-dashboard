package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.ErrorResponse

@ControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun catchAll(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error("An unexpected error occurred", e)
        return ResponseEntity<ErrorResponse>(
            ErrorResponse(
                error = "UNKNOW_ERROR",
                message = "Something went wrong"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}