package app.index_it.daos

import app.index_it.api.plugins.UserSessionId
import app.index_it.core.cache.UserSessionCM
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import org.litote.kmongo.Id

object UserSessionDao {
    fun get(id: String) = UserSessionCM.get(id)

    fun create(id: Id<UserDto>): UserSessionId {
        val userSessionId = UserSessionId(getTimeMillis().toString() +  generateSessionId())

        save(UserSessionDto(
            userSessionId.session_id,
            getTimeMillis(),
            id
        ))

        return userSessionId
    }

    private fun save(userSessionDto: UserSessionDto) = UserSessionCM.create(userSessionDto)

    fun delete(id: String) = UserSessionCM.delete(id)
}
