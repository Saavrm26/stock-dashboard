package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.WatchListOuterClass
import java.util.concurrent.TimeUnit

@Service
class WatchListCacheService(
    private val redisTemplate: RedisTemplate<String, ByteArray>
) {
    fun findByIdOrNull(id: Long): WatchListOuterClass.WatchList? {
        val cacheKey = "api:watchlist:$id"
        val watchListBytes = redisTemplate.opsForValue().get(cacheKey)
        return watchListBytes?.let { WatchListOuterClass.WatchList.parseFrom(watchListBytes) }
    }

    fun saveWatchList(watchList: WatchListOuterClass.WatchList) {
        val cacheKey = "api:watchlist:${watchList.id}"
        redisTemplate.opsForValue().set(cacheKey, watchList.toByteArray(), 5, TimeUnit.MINUTES)
    }
}