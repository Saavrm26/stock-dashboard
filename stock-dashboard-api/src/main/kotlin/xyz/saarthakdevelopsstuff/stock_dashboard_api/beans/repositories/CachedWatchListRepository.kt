package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper

@Component
@Qualifier("cacheAware")
class CachedWatchListRepository(
    private val watchListRepository: WatchListRepository,
    private val redisTemplate: RedisTemplate<String, ByteArray>,
    private val watchListMapper: WatchListMapper
) {
    fun findByIdOrNull(id: Long): WatchListOuterClass.WatchList? {
        val cacheKey = "api:watchlist:$id"
        val watchListBytes = redisTemplate.opsForValue().get(cacheKey)
        if (watchListBytes != null) {
            return WatchListOuterClass.WatchList.parseFrom(watchListBytes)
        }
        val watchListDb = watchListRepository.findByIdOrNull(id) ?: return null
        val watchListCache = watchListMapper.toWatchListProto(watchListDb)
        redisTemplate.opsForValue().set(cacheKey, watchListCache!!.toByteArray())
        return watchListCache
    }
}