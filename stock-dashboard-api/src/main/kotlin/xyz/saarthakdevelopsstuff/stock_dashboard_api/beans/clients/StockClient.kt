package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.clients

import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.properties.StockClientProperties

@Component
class StockClient(stockClientProperties: StockClientProperties) {
    val restClient: RestClient = RestClient.builder().baseUrl(stockClientProperties.url)
        .messageConverters { it.add(ProtobufHttpMessageConverter()) }.build()

    fun getTickerDetails(ticker: String): TickerDetails? {
        return restClient.get().uri {
            it.path("/api/v1/stocks/search").queryParam("query", ticker).build()
        }.retrieve().onStatus({ it.is4xxClientError }) { req, res ->
            throw RuntimeException("Remote service returned ${res.statusCode}")
        }.body<TickerDetails>()
    }

}