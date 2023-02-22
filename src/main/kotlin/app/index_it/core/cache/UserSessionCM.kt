package app.index_it.core.cache

import app.index_it.Env
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id


object UserSessionCM : ExpiringCM("sessions", (Env.session_max_age_in_seconds + 10)) {
    private fun keyValue(userId: Id<UserDto>, sessionId: String) = "${userId}:$sessionId"

    fun get(userId: Id<UserDto>, sessionId: String) : UserSessionDto? = getValue(keyValue(userId, sessionId))

    fun create(userSessionDto: UserSessionDto) = cacheValue(keyValue(userSessionDto.userId, userSessionDto.id), userSessionDto)

    fun delete(userId: Id<UserDto>, sessionId: String) = uncacheValue(keyValue(userId, sessionId))

    fun deleteAll(userId: Id<UserDto>) = uncacheValue("${userId}:*")
}
