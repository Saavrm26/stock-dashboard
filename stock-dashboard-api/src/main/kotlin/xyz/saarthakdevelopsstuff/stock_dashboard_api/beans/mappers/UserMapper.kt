package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import org.mapstruct.Mapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.db.User as DbUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.dto.UserResponse
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.service.User as ServiceUser

@Mapper(componentModel = "spring")
interface UserMapper {
    fun toUserServiceModel(user: DbUser?): ServiceUser?
    fun toUserResponse(user: ServiceUser?): UserResponse
}
