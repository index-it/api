package app.index_it.data.sources.cache.cm.users

import app.index_it.Env
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.core.ExpiringCM


object UserSessionCM : ExpiringCM("sessions", (Env.session_max_age_in_seconds + 10)) {
    private fun keyValue(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = "${userId}:$sessionId"

    fun get(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) : UserAuthSessionDto? = get(
        keyValue(
            userId,
            sessionId
        )
    )

    fun cache(userAuthSessionDto: UserAuthSessionDto) =
        cache(keyValue(userAuthSessionDto.userId, userAuthSessionDto.id), userAuthSessionDto)

    fun delete(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = delete(
        keyValue(
            userId,
            sessionId
        )
    )

    fun deleteAll(userId: IxId<UserDto>) = delete("${userId}:*")
}
