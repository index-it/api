package app.index_it.data.daos.auth

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.auth.UserSessionCookie
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.users.UserSessionCM
import io.ktor.util.date.*

object UserSessionDao {
    fun get(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = UserSessionCM.get(userId, sessionId)

    fun create(userId: IxId<UserDto>, device: String?, ip: String): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(newIxId(), userId)

        save(
            UserAuthSessionDto(
                id = userSessionCookie.sessionId,
                userId = userId,
                iat = currentMillis(),
                deviceName = device,
                ip = ip
            )
        )

        return userSessionCookie
    }

    private fun save(userAuthSessionDto: UserAuthSessionDto) = UserSessionCM.cache(userAuthSessionDto)

    fun delete(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = UserSessionCM.delete(userId, sessionId)

    fun deleteAllSessionsOfUser(userId: IxId<UserDto>) = UserSessionCM.deleteAll(userId)
}
