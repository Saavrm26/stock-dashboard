package xyz.saarthakdevelopsstuff.stock_dashboard_api.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.ErrorResponse

@ControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(StockClientException::class)
    fun handleStockClientExceptions(e: StockClientException): ResponseEntity<ErrorResponse> {
        logger.error("StockClientException occurred", e)
        val errorResponse = when (e.errorCode) {
            StockClientErrorCode.NOT_FOUND -> ErrorResponse(
                error = "NOT_FOUND",
                message = e.description
            )
            StockClientErrorCode.BAD_REQUEST -> ErrorResponse(
                error = "BAD_REQUEST",
                message = e.description
            )
            StockClientErrorCode.RUNTIME_EXCEPTION -> ErrorResponse(
                error = "RUNTIME_EXCEPTION",
                message = e.description
            )
            StockClientErrorCode.DOWNSTREAM_FAILURE -> ErrorResponse(
                error = "DOWNSTREAM_FAILURE",
                message = e.description
            )
        }



        val status = when (e.errorCode) {
            StockClientErrorCode.NOT_FOUND -> HttpStatus.NOT_FOUND
            StockClientErrorCode.BAD_REQUEST -> HttpStatus.BAD_REQUEST
            StockClientErrorCode.RUNTIME_EXCEPTION -> HttpStatus.INTERNAL_SERVER_ERROR
            StockClientErrorCode.DOWNSTREAM_FAILURE -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return ResponseEntity<ErrorResponse>(errorResponse, status)

    }

    @ExceptionHandler(WatchListException::class)
    fun handleWatchListException(e: WatchListException): ResponseEntity<ErrorResponse> {
        logger.error("WatchListException occurred", e)
        val errorResponse = when (e.errorCode) {
            WatchListExceptionErrorCode.NOT_FOUND -> ErrorResponse(
                error = "NOT_FOUND",
                message = e.description
            )
            WatchListExceptionErrorCode.UNAUTHORIZED -> ErrorResponse(
                error = "UNAUTHORIZED",
                message = e.description
            )
            WatchListExceptionErrorCode.USER_NOT_FOUND -> ErrorResponse(
                error = "USER_NOT_FOUND",
                message = e.description
            )
        }

        val status = when (e.errorCode) {
            WatchListExceptionErrorCode.NOT_FOUND -> HttpStatus.NOT_FOUND
            WatchListExceptionErrorCode.UNAUTHORIZED -> HttpStatus.FORBIDDEN
            WatchListExceptionErrorCode.USER_NOT_FOUND -> HttpStatus.NOT_FOUND
        }

        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        logger.error("Validation failed", e)
        val message = e.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }
        return ResponseEntity(
            ErrorResponse(error = "BAD_REQUEST", message = message),
            HttpStatus.BAD_REQUEST
        )
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