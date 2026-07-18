package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import com.google.protobuf.util.JsonFormat
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.TickerDetailsOuterClass.TickerDetails as TickerDetailsProto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker


@Mapper(componentModel = "spring")
interface TickerMapper {

    fun toTickerDetailsProtoFromTickerEntity(ticker: Ticker): TickerDetailsProto {
        // tickerDetails usually contains all the fields from the Ticker
        return mapJsonStringToTickerDetails(ticker.tickerDetails)
    }



    fun mapTickerDetailsProtoToString(ticker: TickerDetailsProto): String {
        return JsonFormat.printer().print(ticker)
    }


    fun mapJsonStringToTickerDetails(jsonString: String): TickerDetailsProto {
        val builder = TickerDetailsProto.newBuilder()
        JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder)
        return builder.build()
    }
}