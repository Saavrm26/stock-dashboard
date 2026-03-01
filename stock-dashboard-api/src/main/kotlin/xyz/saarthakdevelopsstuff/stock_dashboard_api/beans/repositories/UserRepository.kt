package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories

import org.springframework.data.repository.Repository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User

interface UserRepository: Repository<User, String> {
    fun save(user: User): User
    fun findById(id: String): User?
}