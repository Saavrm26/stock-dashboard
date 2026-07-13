package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "watch_list_tickers")
class WatchListTicker(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watch_list_id", nullable = false)
    val watchList: WatchList,

    @Column(name = "ticker_code", nullable = false)
    val tickerCode: String,

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    val createdAt: Instant? = null,

    @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
    val updatedAt: Instant? = null,
)

