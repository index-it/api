package app.index_it.data.sources.cache.cm.users

import app.index_it.data.sources.cache.core.HashedCM
import app.index_it.data.models.user.UserDto
import org.litote.kmongo.Id

object UserCM : HashedCM("users") {
    fun cache(userDto: UserDto) = cache(userDto.id.toString(), userDto)

    fun get(id: Id<UserDto>) : UserDto? = get(id.toString())

    fun delete(id: Id<UserDto>) = delete(id.toString())
}
