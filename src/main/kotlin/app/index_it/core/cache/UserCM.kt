package app.index_it.core.cache

import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object UserCM : HashedCM("users") {
    fun create(userDto: UserDto) = cacheValue(userDto.id.toString(), userDto)

    fun get(id: Id<UserDto>) : UserDto? = getValue(id.toString())

    fun delete(id: Id<UserDto>) = uncacheValue(id.toString())
}
