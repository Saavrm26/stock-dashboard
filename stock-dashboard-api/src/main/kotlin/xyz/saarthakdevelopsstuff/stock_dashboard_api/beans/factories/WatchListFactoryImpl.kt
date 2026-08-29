package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import org.springframework.stereotype.Component
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListVisibility

@Component
class WatchListFactoryImpl : WatchListFactory {
    override fun createEmptyUserWatchList(
        watchListName: String,
        watchListDescription: String,
        user: User,
        visibility: WatchListVisibility
    ): WatchList {
        return WatchList(
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

    override fun createFixedUserWatchList(
        watchListName: String,
        watchListDescription: String,
        user: User,
        visibility: WatchListVisibility,
        tickerSymbols: List<String>
    ): WatchList {
        return WatchList(
            id = null,
            name = watchListName,
            description = watchListDescription,
            createdBy = user.id,
            visibility = visibility,
            type = WatchListType.FIXED,
            screenQuery = null,
            createdAt = null,
            updatedAt = null,
            tickerSymbols = tickerSymbols
        )
    }
}
