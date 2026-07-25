package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility

interface WatchListFactory {
    fun createEmptyUserWatchList(watchListName: String, watchListDescription: String, user: User, visibility: WatchListVisibility = WatchListVisibility.PRIVATE): WatchList
}