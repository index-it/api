package app.index_it.core.cache

import app.index_it.models.user.UserSessionDto
import app.index_it.plugins.UserSessionId
import org.litote.kmongo.Id

object UserSessionCM : HashedCM("sessions") {
    fun get(id: Id<UserSessionId>) : UserSessionDto? = getValue(id.toString())

    fun create(userSessionDto: UserSessionDto) = cacheValue(userSessionDto.id.toString(), userSessionDto)

    fun delete(id: Id<UserSessionId>) = uncacheValue(id.toString())
}
