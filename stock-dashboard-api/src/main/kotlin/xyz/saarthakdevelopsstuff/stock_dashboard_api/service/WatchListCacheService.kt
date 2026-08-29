package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.cache.WatchListOuterClass
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.WatchList
import java.util.concurrent.TimeUnit

@Service
class WatchListCacheService(
    private val redisTemplate: RedisTemplate<String, ByteArray>,
    @Qualifier("setRedisTemplate") private val setRedisTemplate: RedisTemplate<String, String>,
    private val watchListMapper: WatchListMapper
) {
    fun findByIdOrNull(id: Long): WatchList? {
        val cacheKeyWatchList = "api:watchlist:$id"
        val cacheKeyTickers = "api:watchlist:$id:tickers"
        val watchListBytes = redisTemplate.opsForValue().get(cacheKeyWatchList) ?: return null
        val cacheWatchListModel = WatchListOuterClass.WatchList.parseFrom(watchListBytes)
        val watchListTickersSet = setRedisTemplate.opsForSet().members(cacheKeyTickers) ?: return null
        return watchListMapper.toWatchListServiceModel(cacheWatchListModel, watchListTickersSet)
    }

    fun saveWatchList(watchList: WatchList) {
        val cacheKeyWatchList = "api:watchlist:${watchList.id}"
        val cacheKeyTickers = "$cacheKeyWatchList:tickers"
        val cacheModel = watchListMapper.toWatchListCacheModel(watchList)
        redisTemplate.opsForValue().set(cacheKeyWatchList, cacheModel.toByteArray(), 5, TimeUnit.MINUTES)
        if (!watchList.tickerSymbols.isNullOrEmpty()) {
            setRedisTemplate.opsForSet().add(cacheKeyTickers, *watchList.tickerSymbols.toTypedArray())
            setRedisTemplate.expire(cacheKeyTickers, 5, TimeUnit.MINUTES)
        }
    }

    fun evict(id: Long) {
        val cacheKeyWatchList = "api:watchlist:$id"
        val cacheKeyTickers = "$cacheKeyWatchList:tickers"
        redisTemplate.delete(cacheKeyWatchList)
        setRedisTemplate.delete(cacheKeyTickers)
    }
}
