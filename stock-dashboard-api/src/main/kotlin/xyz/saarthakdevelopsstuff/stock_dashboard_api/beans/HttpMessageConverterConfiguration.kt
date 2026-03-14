package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import com.google.protobuf.util.JsonFormat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter

@Configuration
class HttpMessageConverterConfiguration {
    @Bean
    fun protobufJsonFormatHttMessageConverter(): ProtobufJsonFormatHttpMessageConverter {
        return ProtobufJsonFormatHttpMessageConverter(
            JsonFormat.parser().ignoringUnknownFields(), JsonFormat.printer()
        )
    }
}