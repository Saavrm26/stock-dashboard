package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListVisibility

interface WatchListFactory {
    fun createEmptyUserWatchList(
        watchListName: String,
        watchListDescription: String,
        user: User,
        visibility: WatchListVisibility = WatchListVisibility.PRIVATE
    ): WatchList

    fun createFixedUserWatchList(
        watchListName: String,
        watchListDescription: String,
        user: User,
        visibility: WatchListVisibility = WatchListVisibility.PRIVATE,
        tickerSymbols: List<String>
    ): WatchList
}
