package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import java.util.concurrent.TimeUnit

@Service
class WatchListCacheService(
    private val redisTemplate: RedisTemplate<String, ByteArray>,
    private val watchListMapper: WatchListMapper
) {
    fun findByIdOrNull(id: Long): WatchList? {
        val cacheKey = "api:watchlist:$id"
        val watchListBytes = redisTemplate.opsForValue().get(cacheKey) ?: return null
        val cacheModel = WatchListOuterClass.WatchList.parseFrom(watchListBytes)
        return watchListMapper.toWatchListServiceModel(cacheModel)
    }

    fun saveWatchList(watchList: WatchList) {
        val cacheKey = "api:watchlist:${watchList.id}"
        val cacheModel = watchListMapper.toWatchListCacheModel(watchList)
        redisTemplate.opsForValue().set(cacheKey, cacheModel.toByteArray(), 5, TimeUnit.MINUTES)
    }
}
