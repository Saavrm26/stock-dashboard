package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.TickerMetadata

@Converter
class TickerMetadataConverter : AttributeConverter<TickerMetadata, String> {
    private val objectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    override fun convertToDatabaseColumn(tickerMetadata: TickerMetadata?): String? {
        try {
            return objectMapper.writeValueAsString(tickerMetadata)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Failed to convert TickerMetadata to JSON", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): TickerMetadata? {
        if (dbData == null) return null
        try {
            return objectMapper.readValue(dbData)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Failed to convert JSON to TickerMetadata", e)
        }
    }
}