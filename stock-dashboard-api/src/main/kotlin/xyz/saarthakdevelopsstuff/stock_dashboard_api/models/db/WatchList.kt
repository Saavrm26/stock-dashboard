package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "watch_list")
class WatchList(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,

    @Column(name = "name", nullable = false) val name: String,

    // DB migration declares description NOT NULL
    @Column(name = "description", nullable = false) val description: String,

    // created_by references users.id (text).
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by") val createdBy: User?,

    @Enumerated(EnumType.STRING) @Column(nullable = false) val visibility: WatchListVisibility = WatchListVisibility.PRIVATE,

    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false) val type: WatchListType = WatchListType.FIXED,

    @Column(name = "screen_query") val screenQuery: String? = null,

    // Generated ts_vector column - read-only from JPA side
    @Column(
        name = "search_vector", insertable = false, updatable = false, columnDefinition = "tsvector"
    ) val searchVector: String? = null,

) {

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false) val createdAt: Instant? = null

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false) val updatedAt: Instant? = null
}

enum class WatchListVisibility { PRIVATE, PUBLIC }

enum class WatchListType { FIXED, DYNAMIC }
