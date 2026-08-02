package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import org.springframework.stereotype.Component
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility

@Component
class WatchListFactoryImpl : WatchListFactory {
    override fun createEmptyUserWatchList(
        watchListName: String,
        watchListDescription: String,
        user: xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User,
        visibility: WatchListVisibility
    ): xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList {
        return xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList(
            id = null,
            name = watchListName,
            description = watchListDescription,
            createdBy = user.id,
            visibility = visibility,
            type = WatchListType.FIXED,
            screenQuery = null,
            createdAt = null,
            updatedAt = null,
            tickerSymbols = null
        )
    }
}
