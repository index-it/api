package app.index_it.data.daos.auth

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.auth.UserSessionCookie
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.users.UserSessionCM
import app.index_it.data.sources.cache.cm.users.impl.UserSessionCMImpl
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserSessionDao(
    private val userSessionCM: UserSessionCM
) {
    fun get(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = userSessionCM.get(userId, sessionId)

    fun create(userId: IxId<UserDto>, device: String?, ip: String): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(newIxId(), userId)

        save(
            UserAuthSessionDto(
                id = userSessionCookie.sessionId,
                userId = userId,
                iat = DatetimeUtils.currentMillis(),
                deviceName = device,
                ip = ip
            )
        )

        return userSessionCookie
    }

    private fun save(userAuthSessionDto: UserAuthSessionDto) = userSessionCM.cache(userAuthSessionDto)

    fun delete(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = userSessionCM.delete(userId, sessionId)

    fun deleteAllSessionsOfUser(userId: IxId<UserDto>) = userSessionCM.deleteAll(userId)
}
