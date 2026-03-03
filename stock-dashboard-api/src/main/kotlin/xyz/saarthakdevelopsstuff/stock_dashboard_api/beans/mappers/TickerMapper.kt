package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.Ticker
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.CreateTickerRequest


@Mapper(componentModel = "spring")
interface TickerMapper {
    fun createTickerRequestToTicker(createTickerRequest: CreateTickerRequest): Ticker
}