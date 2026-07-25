package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import org.mapstruct.Mapper
import org.springframework.beans.factory.annotation.Autowired
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails as TickerDetailsProto
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerSearch.Quote as QuoteProto
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerSearch.TickerSearchResponse as TickerSearchResponseProto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsListResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerDetailsResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerQuoteResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.TickerSearchResponseDto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerQuote
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.TickerSearchResult

@Mapper(componentModel = "spring")
abstract class TickerMapper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    fun toTickerDetails(proto: TickerDetailsProto): TickerDetails {
        return TickerDetails(
            symbol = proto.symbol,
            exchange = if (proto.hasExchange()) proto.exchange else null,
            tickerName = if (proto.hasShortName()) proto.shortName else null,
            industry = if (proto.hasIndustry()) proto.industry else null,
            marketCap = if (proto.hasMarketCap()) proto.marketCap else null,
            price = if (proto.hasCurrentPrice()) proto.currentPrice else null
        )
    }

    fun toTickerDetails(ticker: Ticker): TickerDetails {
        return objectMapper.readValue(ticker.tickerDetails, TickerDetails::class.java)
    }

    abstract fun toTickerDetailsResponse(details: TickerDetails): TickerDetailsResponse

    fun toTickerDetailsListResponse(details: List<TickerDetails>): TickerDetailsListResponse {
        return TickerDetailsListResponse(tickers = details.map { toTickerDetailsResponse(it) })
    }

    abstract fun toTickerQuote(quote: QuoteProto): TickerQuote

    abstract fun toTickerQuoteResponse(quote: TickerQuote): TickerQuoteResponse

    fun toTickerSearchResult(proto: TickerSearchResponseProto): TickerSearchResult {
        return TickerSearchResult(quotes = proto.quotesList.map { toTickerQuote(it) })
    }

    fun toTickerSearchResponseDto(result: TickerSearchResult): TickerSearchResponseDto {
        return TickerSearchResponseDto(quotes = result.quotes.map { toTickerQuoteResponse(it) })
    }


}
