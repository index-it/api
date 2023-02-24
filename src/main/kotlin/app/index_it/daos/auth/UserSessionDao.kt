package app.index_it.daos.auth

import app.index_it.api.plugins.UserSessionCookie
import app.index_it.core.cache.UserSessionCM
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import org.litote.kmongo.Id

// TODO: Auto delete from redis after expire time
object UserSessionDao {
    fun get(userId: Id<UserDto>, sessionId: String) = UserSessionCM.get(userId, sessionId)

    fun create(userId: Id<UserDto>, device: String?, ip: String): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(getTimeMillis().toString() + generateSessionId(), userId.toString())

        save(
            UserSessionDto(
                id = userSessionCookie.session_id,
                userId = userId,
                iat = getTimeMillis(),
                deviceName = device,
                ip = ip
            )
        )

        return userSessionCookie
    }

    private fun save(userSessionDto: UserSessionDto) = UserSessionCM.create(userSessionDto)

    fun delete(userId: Id<UserDto>, sessionId: String) = UserSessionCM.delete(userId, sessionId)

    fun deleteAllSessionsOfUser(userId: Id<UserDto>) = UserSessionCM.deleteAll(userId)
}
