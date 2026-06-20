package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import com.fasterxml.jackson.datatype.jsonp.JSONPModule
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

    @Bean
    fun jsonPJacksonModule() : JSONPModule {
        return JSONPModule()
    }

//    @Bean
//    fun addJacksonModules() = Jackson2ObjectMapperBuilderCustomizer {
//        builder -> builder.modules { listOf(ProtobufModule(), JSONPModule()) }
//    }
}