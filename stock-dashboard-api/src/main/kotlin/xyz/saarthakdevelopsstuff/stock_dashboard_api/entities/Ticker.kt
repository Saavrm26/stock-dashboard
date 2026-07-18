package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "tickers")
class Ticker(
    @Id
    @Column(name = "ticker_code")
    var tickerCode: String,

    @Column(name = "ticker_long_name")
    var tickerLongName: String,

    @Column(name = "ticker_exchange")
    var tickerExchange: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ticker_details", columnDefinition = "jsonb")
    var tickerDetails: String
)
