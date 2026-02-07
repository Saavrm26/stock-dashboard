package xyz.saarthakdevelopsstuff.stock_dashboard_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ConfigurationPropertiesScan
class StockDashboardApiApplication

fun main(args: Array<String>) {
	runApplication<StockDashboardApiApplication>(*args)
}
