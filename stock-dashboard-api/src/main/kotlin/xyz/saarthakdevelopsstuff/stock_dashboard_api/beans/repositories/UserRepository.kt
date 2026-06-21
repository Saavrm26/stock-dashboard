package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User
import java.util.Optional

interface UserRepository: JpaRepository<User, String> {
    fun save(user: User): User
    override fun findById(id: String): Optional<User?>
}