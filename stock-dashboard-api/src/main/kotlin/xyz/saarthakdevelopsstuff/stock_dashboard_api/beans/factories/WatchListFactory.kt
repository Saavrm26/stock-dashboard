package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList

interface WatchListFactory {
    fun createEmptyUserWatchList(watchListName: String, watchListDescription: String, user: User): WatchList
}