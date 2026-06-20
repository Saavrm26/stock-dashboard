package xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id var id: String,
    @Column(nullable = false) var email: String,
    @Column(name = "full_name", nullable = false) var fullName: String
)