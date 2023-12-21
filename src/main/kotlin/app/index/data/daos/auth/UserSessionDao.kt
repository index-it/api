package app.index.data.daos.auth

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.auth.UserAuthSessionDto
import app.index.data.models.auth.UserSessionCookie
import app.index.data.models.user.UserDto
import app.index.data.sources.cache.cm.users.UserSessionCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserSessionDao(
    private val userSessionCM: UserSessionCM,
) {
    fun get(
        userId: IxId<UserDto>,
        sessionId: IxId<UserAuthSessionDto>,
    ) = userSessionCM.get(userId, sessionId)

    fun create(
        userId: IxId<UserDto>,
        device: String?,
        ip: String,
    ): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(newIxId(), userId)

        save(
            UserAuthSessionDto(
                id = userSessionCookie.sessionId,
                userId = userId,
                iat = DatetimeUtils.currentMillis(),
                deviceName = device,
                ip = ip,
            ),
        )

        return userSessionCookie
    }

    private fun save(userAuthSessionDto: UserAuthSessionDto) = userSessionCM.cache(userAuthSessionDto)

    fun delete(
        userId: IxId<UserDto>,
        sessionId: IxId<UserAuthSessionDto>,
    ) = userSessionCM.delete(userId, sessionId)

    fun deleteAllSessionsOfUser(userId: IxId<UserDto>) = userSessionCM.deleteAll(userId)
}
