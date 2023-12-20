package app.index_it.data.sources.cache.cm.users

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto

interface UserCM {
    fun cache(userDto: UserDto)

    fun get(id: IxId<UserDto>) : UserDto?

    fun delete(id: IxId<UserDto>)
}