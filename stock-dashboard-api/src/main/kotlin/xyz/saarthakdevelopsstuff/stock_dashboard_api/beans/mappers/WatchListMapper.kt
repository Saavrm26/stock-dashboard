package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import com.google.protobuf.Timestamp
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass.WatchList as WatchListCacheProto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass.WatchListType as ProtoWatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass.WatchListVisibility as ProtoWatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList as ServiceWatchList

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = [TickerMapper::class])
interface WatchListMapper {

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    fun toWatchListServiceModel(watchList: DbWatchList, tickers: List<Ticker>): ServiceWatchList

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    fun toWatchListServiceModel(watchList: DbWatchList): ServiceWatchList

    fun toWatchListServiceModel(cacheModel: WatchListCacheProto): ServiceWatchList

    fun toWatchListCacheModel(watchList: ServiceWatchList): WatchListCacheProto

    fun toWatchListResponse(watchList: ServiceWatchList): WatchListResponse

    fun toInstant(timestamp: Timestamp?): java.time.Instant? {
        return timestamp?.let { java.time.Instant.ofEpochSecond(it.seconds, it.nanos.toLong()) }
    }

    fun toTimestamp(instant: java.time.Instant?): Timestamp? {
        return instant?.let { Timestamp.newBuilder().setSeconds(it.epochSecond).setNanos(it.nano).build() }
    }

    fun toVisibility(visibility: ProtoWatchListVisibility): WatchListVisibility {
        return when (visibility) {
            ProtoWatchListVisibility.PUBLIC -> WatchListVisibility.PUBLIC
            ProtoWatchListVisibility.PRIVATE -> WatchListVisibility.PRIVATE
            ProtoWatchListVisibility.UNRECOGNIZED -> WatchListVisibility.PRIVATE
        }
    }

    fun toProtoVisibility(visibility: WatchListVisibility): ProtoWatchListVisibility {
        return when (visibility) {
            WatchListVisibility.PUBLIC -> ProtoWatchListVisibility.PUBLIC
            WatchListVisibility.PRIVATE -> ProtoWatchListVisibility.PRIVATE
        }
    }

    fun toType(type: ProtoWatchListType): WatchListType {
        return when (type) {
            ProtoWatchListType.DYNAMIC -> WatchListType.DYNAMIC
            ProtoWatchListType.FIXED -> WatchListType.FIXED
            ProtoWatchListType.UNRECOGNIZED -> WatchListType.FIXED
        }
    }

    fun toProtoType(type: WatchListType): ProtoWatchListType {
        return when (type) {
            WatchListType.DYNAMIC -> ProtoWatchListType.DYNAMIC
            WatchListType.FIXED -> ProtoWatchListType.FIXED
        }
    }
}
