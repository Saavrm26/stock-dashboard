package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse

@Mapper(componentModel = "spring")
interface WatchListMapper {

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    fun toWatchListResponse(watchList: WatchList): WatchListResponse
}
