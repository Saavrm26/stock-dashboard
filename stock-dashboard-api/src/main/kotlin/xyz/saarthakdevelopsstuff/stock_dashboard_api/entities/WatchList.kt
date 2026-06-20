package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities

import jakarta.persistence.*
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

    @Column(name = "created_at", nullable = false, updatable = false) val createdAt: Instant? = null,

    @Column(name = "updated_at", nullable = false) var updatedAt: Instant? = null
)

enum class WatchListVisibility { PRIVATE, PUBLIC }

enum class WatchListType { FIXED, DYNAMIC }
