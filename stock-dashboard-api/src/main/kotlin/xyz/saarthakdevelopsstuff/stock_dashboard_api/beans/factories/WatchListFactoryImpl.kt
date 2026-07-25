package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import org.springframework.stereotype.Component
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility

@Component
class WatchListFactoryImpl : WatchListFactory {
    override fun createEmptyUserWatchList(watchListName: String, watchListDescription: String, user: User, visibility: WatchListVisibility): WatchList {
        return WatchList(
            name = watchListName,
            description = watchListDescription,
            visibility = visibility,
            createdBy = user
        )
    }
}