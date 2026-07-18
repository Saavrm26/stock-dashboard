package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {
    // Protobuf addon
    @Bean
    fun protobufJacksonModule() : ProtobufModule {
        return ProtobufModule()
    }
}