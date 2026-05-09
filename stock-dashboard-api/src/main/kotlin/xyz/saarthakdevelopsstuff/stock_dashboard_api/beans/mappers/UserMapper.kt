package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers

import org.mapstruct.Mapper
import xyz.saarthakdevelopsstuff.stock_dashboard.common.proto.v1.UserDto
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.User

@Mapper(componentModel = "spring")
interface UserMapper {
    fun toUserDto(user: User?): UserDto.User
}