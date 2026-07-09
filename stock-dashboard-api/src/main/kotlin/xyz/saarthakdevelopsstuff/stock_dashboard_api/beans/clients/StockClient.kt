package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetailsList
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerSearch.TickerSearchResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties.StockClientProperties
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.StockClientException


class StockClientErrorHandler : RestClient.ResponseSpec.ErrorHandler {
    private val logger = LoggerFactory.getLogger(StockClientErrorHandler::class.java)
    override fun handle(request: HttpRequest, response: ClientHttpResponse) {
        if (response.statusCode == HttpStatus.NOT_FOUND) {
            throw StockClientException(
                StockClientErrorCode.NOT_FOUND, """
                Resource doesn't exists. URI: ${request.uri}, Method: ${request.method}.
                Downstream response: ${response.body}
                """.trimIndent()
            )
        }
        if (response.statusCode == HttpStatus.BAD_REQUEST) {
            throw StockClientException(
                StockClientErrorCode.BAD_REQUEST, """
                BadRequest for URI: ${request.uri}, Method: ${request.method}.
                Downstream response: ${response.body}
                """.trimIndent()
            )
        }
        throw StockClientException(
            StockClientErrorCode.RUNTIME_EXCEPTION, """
            Unknow error for URI: ${request.uri}, Method: ${request.method}.
            Downstream response: ${response.body}
            """.trimIndent()
        )
    }
}

@Component
class StockClient(restClientBuilder: RestClient.Builder, stockClientProperties: StockClientProperties) {
     val restClient: RestClient = restClientBuilder.baseUrl(stockClientProperties.url)
        .messageConverters { it.add(ProtobufHttpMessageConverter()) }.defaultStatusHandler(
            HttpStatusCode::isError, StockClientErrorHandler()
        ).build()

    fun getTickerDetails(ticker: String): TickerDetails? {
        return restClient.get().uri {
            it.path("/api/v1/stocks/ticker-details").queryParam("query", ticker).build()
        }.retrieve().body<TickerDetails>()
    }

    fun searchTickers(query: String): TickerSearchResponse? {
        return restClient.get().uri {
            it.path("/api/v1/stocks/search").queryParam("query", query).build()
        }.retrieve().body<TickerSearchResponse>()
    }

    fun getBulkTickerDetails(tickers: List<String>): TickerDetailsList {
        val tickers = restClient.post()
            .uri { it.path("/api/v1/stocks/bulk/ticker-details").build() }
            .body(tickers)
            .retrieve()
            .body<TickerDetailsList>() ?: throw StockClientException(
            StockClientErrorCode.DOWNSTREAM_FAILURE, """
                Downstream api response is malformed.
            """.trimIndent()
            )
        return tickers
    }

}