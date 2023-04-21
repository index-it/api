package app.index_it.daos.auth

import app.index_it.core.cache.UserSessionCM
import app.index_it.models.auth.UserAuthSessionDto
import app.index_it.models.auth.UserSessionCookie
import app.index_it.models.user.UserDto
import io.ktor.util.date.*
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

object UserSessionDao {
    fun get(userId: Id<UserDto>, sessionId: Id<UserAuthSessionDto>) = UserSessionCM.get(userId, sessionId)

    fun create(userId: Id<UserDto>, device: String?, ip: String): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(ObjectId().toId(), userId)

        save(
            UserAuthSessionDto(
                id = userSessionCookie.sessionId,
                userId = userId,
                iat = getTimeMillis(),
                deviceName = device,
                ip = ip
            )
        )

        return userSessionCookie
    }

    private fun save(userAuthSessionDto: UserAuthSessionDto) = UserSessionCM.cache(userAuthSessionDto)

    fun delete(userId: Id<UserDto>, sessionId: Id<UserAuthSessionDto>) = UserSessionCM.delete(userId, sessionId)

    fun deleteAllSessionsOfUser(userId: Id<UserDto>) = UserSessionCM.deleteAll(userId)
}
