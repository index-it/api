package app.index_it.data.sources.cache.cm.users.impl

import app.index_it.config.ApiConfig
import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.users.UserSessionCM
import app.index_it.data.sources.cache.core.ExpiringCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [UserSessionCM::class])
class UserSessionCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper
) : UserSessionCM,
    ExpiringCM(
    keyBase = "sessions",
    expirationInSeconds = (ApiConfig.sessionMaxAgeInSeconds + 10),
    redisClient,
    objectMapper
) {
    private fun keyValue(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = "${userId}:$sessionId"

    override fun get(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) : UserAuthSessionDto? = get(
        keyValue(
            userId,
            sessionId
        )
    )

    override fun cache(userAuthSessionDto: UserAuthSessionDto) =
        cache(keyValue(userAuthSessionDto.userId, userAuthSessionDto.id), userAuthSessionDto)

    override fun delete(userId: IxId<UserDto>, sessionId: IxId<UserAuthSessionDto>) = delete(
        keyValue(
            userId,
            sessionId
        )
    )

    override fun deleteAll(userId: IxId<UserDto>) = delete("${userId}:*")
}
