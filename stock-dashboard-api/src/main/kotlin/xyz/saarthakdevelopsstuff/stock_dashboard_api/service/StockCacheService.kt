package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails as TickerDetailsProto
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetailsList as TickerDetailsListProto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.TickerMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails

@Service
class StockCacheService(
    private val redisTemplate: RedisTemplate<String, ByteArray>,
    private val tickerMapper: TickerMapper
) {
    companion object StockCacheService {
        private const val PREFIX = "api:ticker:"
    }

    fun getBulkTickerDetails(tickerCodes: List<String>): List<TickerDetails> {
        val cacheKeys = tickerCodes.map { PREFIX + it }
        val tickersCacheResponse = redisTemplate.opsForValue().multiGet(cacheKeys)
        return tickersCacheResponse?.mapNotNull {
            it?.let { bytes -> tickerMapper.toTickerDetails(TickerDetailsProto.parseFrom(bytes)) }
        } ?: emptyList()
    }

    fun setBulkTickerDetails(tickerDetailsList: TickerDetailsListProto) {
        val tickersCache = tickerDetailsList.tickersList.associate { PREFIX + it.symbol to it.toByteArray() }
        redisTemplate.opsForValue().multiSet(tickersCache)
    }
}
