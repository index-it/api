package app.index_it.data.sources.cache.cm.users

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.core.HashedCM

object UserCM : HashedCM("users") {
    fun cache(userDto: UserDto) = cache(userDto.id.toString(), userDto)

    fun get(id: IxId<UserDto>) : UserDto? = get(id.toString())

    fun delete(id: IxId<UserDto>) = delete(id.toString())
}
