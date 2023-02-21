package app.index_it.daos

import app.index_it.api.plugins.UserSessionId
import app.index_it.core.cache.UserSessionCM
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import org.litote.kmongo.Id

// TODO: Auto delete from redis after expire time
object UserSessionDao {
    fun get(userId: Id<UserDto>, sessionId: String) = UserSessionCM.get(userId, sessionId)

    fun create(id: Id<UserDto>): UserSessionId {
        val userSessionId = UserSessionId(getTimeMillis().toString() +  generateSessionId(), id.toString())

        save(
            id,
            UserSessionDto(
                userSessionId.session_id,
                getTimeMillis(),
                id
            )
        )

        return userSessionId
    }

    private fun save(userId: Id<UserDto>, userSessionDto: UserSessionDto) = UserSessionCM.create(userId, userSessionDto)

    fun delete(userId: Id<UserDto>, sessionId: String) = UserSessionCM.delete(userId, sessionId)

    fun deleteAllSessionsOfUser(userId: Id<UserDto>) = UserSessionCM.deleteAll(userId)
}
