package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import com.google.protobuf.Timestamp
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.factory.Mappers
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass.WatchList as WatchListCacheProto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass.WatchListType as ProtoWatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass.WatchListVisibility as ProtoWatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchList as DbWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListTicker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListType
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.WatchListVisibility
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.WatchListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User as ServiceUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList as ServiceWatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchListAggregate

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = [TickerMapper::class, UserMapper::class])
interface WatchListMapper {

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    @Mapping(target = "tickerSymbols", source = "tickers")
    fun toWatchListServiceModelFromDbWatchListAndTickers(watchList: DbWatchList, tickers: List<Ticker>): ServiceWatchList

    fun toDbWatchList(watchList: ServiceWatchList, user: ServiceUser): DbWatchList {
        val dbUser = Mappers.getMapper(UserMapper::class.java).toDbUser(user)
        return DbWatchList(
            id = watchList.id,
            name = watchList.name,
            description = watchList.description,
            createdBy = dbUser,
            visibility = watchList.visibility,
            type = watchList.type,
            screenQuery = watchList.screenQuery
        )
    }

    fun toDbWatchListTickers(watchList: DbWatchList, tickerSymbols: List<String>): List<WatchListTicker> {
        return tickerSymbols.map { WatchListTicker(watchList = watchList, tickerCode = it) }
    }

    @Mapping(target = "createdBy", source = "watchList.createdBy.id")
    @Mapping(target = "tickerSymbols", source = "tickerSymbols")
    fun toWatchListServiceModel(watchList: DbWatchList, tickerSymbols: List<String>): ServiceWatchList

    @Mapping(target = "tickerSymbols", source = "tickerSymbols")
    fun toWatchListServiceModel(cacheModel: WatchListCacheProto, tickerSymbols: Set<String>): ServiceWatchList

    fun toWatchListCacheModel(watchList: ServiceWatchList): WatchListCacheProto = WatchListCacheProto.newBuilder()
        .setId(watchList.id ?: 0)
        .setName(watchList.name)
        .setDescription(watchList.description)
        .apply { watchList.createdBy?.let { setCreatedBy(it) } }
        .setVisibility(toProtoVisibility(watchList.visibility))
        .setType(toProtoType(watchList.type))
        .apply { watchList.screenQuery?.let { setScreenQuery(it) } }
        .apply { watchList.createdAt?.let { setCreatedAt(toTimestamp(it)) } }
        .apply { watchList.updatedAt?.let { setUpdatedAt(toTimestamp(it)) } }
        .build()

    @Mapping(target = "tickers", source = "tickerDetails")
    fun toWatchListAggregate(watchList: ServiceWatchList, tickerDetails: List<TickerDetails>): WatchListAggregate

    fun toWatchListResponse(watchList: WatchListAggregate): WatchListResponse

    fun tickerToSymbol(ticker: Ticker): String = ticker.tickerCode

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
