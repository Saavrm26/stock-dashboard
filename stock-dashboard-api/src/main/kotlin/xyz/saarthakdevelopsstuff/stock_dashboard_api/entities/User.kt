package xyz.saarthakdevelopsstuff.stock_dashboard_api.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id val id: String,
    @Column(nullable = false) val email: String,
    @Column(name = "full_name", nullable = false) val fullName: String
)