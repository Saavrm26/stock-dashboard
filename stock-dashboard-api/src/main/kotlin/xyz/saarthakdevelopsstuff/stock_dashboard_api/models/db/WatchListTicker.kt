package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "watch_list_tickers")
class WatchListTicker(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "watch_list_id", nullable = false)
    val watchList: WatchList,

    @Column(name = "ticker_code", nullable = false)
    val tickerCode: String,

) {

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false) val createdAt: Instant? = null

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false) val updatedAt: Instant? = null
}

