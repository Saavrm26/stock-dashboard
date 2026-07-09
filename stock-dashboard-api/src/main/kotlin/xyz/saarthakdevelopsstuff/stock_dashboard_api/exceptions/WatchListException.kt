package xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions

enum class WatchListExceptionErrorCode(val code: String) {
    NOT_FOUND("WATCHLIST_NOT_FOUND"),
    UNAUTHORIZED("WATCHLIST_ACTION_UNAUTHORIZED"),
    USER_NOT_FOUND("WATCHLIST_USER_NOT_FOUND"),
}

class WatchListException(val errorCode: WatchListExceptionErrorCode, val description : String) : Exception("Code: ${errorCode.code} | Message: $description")
