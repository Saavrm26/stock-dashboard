package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.tickerDetailsList
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetailsList as TickerDetailsListProto
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails as TickerDetailsProto

@Service
class StockCacheService(private val redisTemplate: RedisTemplate<String, ByteArray>) {
    companion object StockCacheService {
        private const val PREFIX = "api:ticker:"

    }
    fun getBulkTickerDetails(tickerCodes: List<String>) : TickerDetailsListProto {
        val cacheKeys = tickerCodes.map { PREFIX + it }
        val tickersCacheResponse = redisTemplate.opsForValue().multiGet(cacheKeys)
        val tickersResponse = tickersCacheResponse?.map { TickerDetailsProto.parseFrom(it  ) } ?: emptyList()
        return tickerDetailsList {
            tickers += tickersResponse
        }
    }

    fun setBulkTickerDetails(tickerDetailsList: TickerDetailsListProto) {
        val tickersCache = tickerDetailsList.tickersList.associate { PREFIX + it.symbol to it.toByteArray() }
        redisTemplate.opsForValue().multiSet(tickersCache)
    }
}