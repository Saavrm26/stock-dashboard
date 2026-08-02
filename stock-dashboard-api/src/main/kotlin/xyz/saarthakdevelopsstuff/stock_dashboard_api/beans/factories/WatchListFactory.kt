package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility

interface WatchListFactory {
    fun createEmptyUserWatchList(
        watchListName: String,
        watchListDescription: String,
        user: xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User,
        visibility: WatchListVisibility = WatchListVisibility.PRIVATE
    ): xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
}
