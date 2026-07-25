package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id val id: String,
    @Column(nullable = false) val email: String,
    @Column(name = "full_name", nullable = false) val fullName: String
)