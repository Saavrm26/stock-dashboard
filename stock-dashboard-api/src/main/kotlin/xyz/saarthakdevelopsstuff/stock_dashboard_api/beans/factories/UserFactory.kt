package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories

import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User

interface UserFactory {
    fun createUser(name: String, username: String, email: String) : User
}