package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnTransformer
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.converters.TickerMetadataConverter
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.TickerMetadata
import java.util.*

@Entity
@Table(name = "tickers")
class Ticker(
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID,
    @Column("ticker_code") var tickerCode: String,
    @Column("ticker_long_name") var tickerLongName: String,
    @Column("ticker_exchange") var tickerExchange: String,
    @Column(
        "ticker_metadata", columnDefinition = "jsonb"
    ) @Convert(converter = TickerMetadataConverter::class) @ColumnTransformer(write = "?::jsonb") var tickerMetadata: TickerMetadata
)