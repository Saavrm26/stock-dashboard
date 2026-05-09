package xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions

enum class StockClientErrorCode(val code: String) {
    NOT_FOUND("NOT_FOUND"),
    BAD_REQUEST("BAD_REQUEST"),
    RUNTIME_EXCEPTION("RUNTIME_EXCEPTION")
}

class StockClientException(
    val errorCode: StockClientErrorCode,
    val description: String
) : Exception("Code: ${errorCode.code} | Message: $description")
