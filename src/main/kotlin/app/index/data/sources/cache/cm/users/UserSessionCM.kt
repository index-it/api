package app.index.data.sources.cache.cm.users

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.auth.UserAuthSessionDto
import app.index.data.models.user.UserDto

interface UserSessionCM {
    fun get(
        userId: IxId<UserDto>,
        sessionId: IxId<UserAuthSessionDto>,
    ): UserAuthSessionDto?

    fun cache(userAuthSessionDto: UserAuthSessionDto)

    fun delete(
        userId: IxId<UserDto>,
        sessionId: IxId<UserAuthSessionDto>,
    )

    fun deleteAll(userId: IxId<UserDto>)
}
