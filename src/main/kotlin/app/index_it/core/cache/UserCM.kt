package app.index_it.core.cache

import app.index_it.models.user.UserDto

object UserCM : HashedCM("users") {
    fun create(userDto: UserDto) = cacheValue(userDto.id, userDto)

    fun get(id: String) : UserDto? = getValue(id)

    fun delete(id: String) = uncacheValue(id)
}
