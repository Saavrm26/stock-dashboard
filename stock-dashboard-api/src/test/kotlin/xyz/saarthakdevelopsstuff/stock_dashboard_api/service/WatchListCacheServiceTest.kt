package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.redis.core.RedisTemplate
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper

class WatchListCacheServiceTest {

    private val redisTemplate = mock<RedisTemplate<String, ByteArray>>()
    private val setRedisTemplate = mock<RedisTemplate<String, String>>()
    private val watchListMapper = mock<WatchListMapper>()

    private val cacheService = WatchListCacheService(
        redisTemplate = redisTemplate,
        setRedisTemplate = setRedisTemplate,
        watchListMapper = watchListMapper
    )

    @Test
    fun `evict deletes both the watchlist key and the tickers set key`() {
        cacheService.evict(42L)

        verify(redisTemplate).delete("api:watchlist:42")
        verify(setRedisTemplate).delete("api:watchlist:42:tickers")
    }
}
