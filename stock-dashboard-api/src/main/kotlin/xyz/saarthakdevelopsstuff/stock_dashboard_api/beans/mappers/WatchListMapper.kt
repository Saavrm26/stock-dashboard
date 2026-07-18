package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import com.google.protobuf.Timestamp
import org.mapstruct.Condition
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass.WatchList as WatchListProto
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass.WatchListType as ProtoWatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass.WatchListVisibility as ProtoWatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = [TickerMapper::class])
interface WatchListMapper {

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    fun toWatchListResponse(watchList: WatchList, tickers: List<Ticker>): WatchListResponse

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    fun toWatchListProto(watchList: WatchList?): WatchListProto?

    fun toWatchListResponse(watchListCache: WatchListProto): WatchListResponse

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
