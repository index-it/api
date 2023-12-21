package app.index.data.sources.cache.cm.users

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserDto

interface UserCM {
    fun cache(userDto: UserDto)

    fun get(id: IxId<UserDto>): UserDto?

    fun delete(id: IxId<UserDto>)
}
