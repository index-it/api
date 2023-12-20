package app.index_it.data.sources.cache.cm.users

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto

interface UserSessionCM {
    fun get(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) : UserAuthSessionDto?

    fun cache(userAuthSessionDto: UserAuthSessionDto)

    fun delete(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>)

    fun deleteAll(userId: IxId<UserDto>)
}