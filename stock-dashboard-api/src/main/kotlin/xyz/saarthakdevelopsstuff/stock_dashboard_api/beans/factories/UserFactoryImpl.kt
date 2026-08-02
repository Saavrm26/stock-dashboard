package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import org.springframework.stereotype.Component
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User

@Component
class UserFactoryImpl : UserFactory {
    override fun createUser(name: String, username: String, email: String): User {
        val user = User(id = username, email = email, fullName = name)
        return user
    }
}