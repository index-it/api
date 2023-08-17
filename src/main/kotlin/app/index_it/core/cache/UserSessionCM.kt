package app.index_it.core.cache

import app.index_it.Env
import app.index_it.core.cache.core.ExpiringCM
import app.index_it.models.auth.UserAuthSessionDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id


object UserSessionCM : ExpiringCM("sessions", (Env.session_max_age_in_seconds + 10)) {
    private fun keyValue(userId: Id<UserDto>, sessionId: Id<UserAuthSessionDto>) = "${userId}:$sessionId"

    fun get(userId: Id<UserDto>, sessionId: Id<UserAuthSessionDto>) : UserAuthSessionDto? = get(keyValue(userId, sessionId))

    fun cache(userAuthSessionDto: UserAuthSessionDto) =
        cache(keyValue(userAuthSessionDto.userId, userAuthSessionDto.id), userAuthSessionDto)

    fun delete(userId: Id<UserDto>, sessionId: Id<UserAuthSessionDto>) = delete(keyValue(userId, sessionId))

    fun deleteAll(userId: Id<UserDto>) = delete("${userId}:*")
}
