package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.models.TickerDetails
import java.time.Instant

@Entity
@Table(name = "watch_list_tickers")
class WatchListTicker(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "watch_list_id", nullable = false) val watchList: WatchList,

    @Column(name = "ticker_code", nullable = false) val tickerCode: String,

    @Column(name = "ticker_long_name", nullable = false) val tickerLongName: String,

    @Column(name = "ticker_exchange", nullable = false) val tickerExchange: String,

    // Use Hibernate 6 JSON mapping to map JSONB -> TickerDetails directly
    @JdbcTypeCode(SqlTypes.JSON) @Column(
        name = "ticker_details",
        nullable = false,
        columnDefinition = "jsonb"
    ) val tickerDetails: TickerDetails,

    @Column(name = "created_at", nullable = false, updatable = false) val createdAt: Instant? = null,

    @Column(name = "updated_at", nullable = false) var updatedAt: Instant? = null,
)

