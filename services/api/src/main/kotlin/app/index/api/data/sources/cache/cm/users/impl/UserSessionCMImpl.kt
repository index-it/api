package app.index.api.data.sources.cache.cm.users.impl

import app.index.api.config.ApiConfig
import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData
import app.index.api.data.sources.cache.cm.users.UserSessionCM
import app.index.api.data.sources.cache.core.ExpiringCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [UserSessionCM::class])
class UserSessionCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : UserSessionCM,
    ExpiringCM(
        keyBase = "sessions",
        expirationInSeconds = (ApiConfig.sessionMaxAgeInSeconds + 10),
        redisClient,
        objectMapper,
    ) {
    private fun keyValue(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    ) = "$userId:$sessionId"

    override fun get(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    ): UserAuthSessionData? =
        get(
            keyValue(
                userId,
                sessionId,
            ),
        )

    override fun cache(userAuthSessionData: UserAuthSessionData) =
        cache(keyValue(userAuthSessionData.userId, userAuthSessionData.id), userAuthSessionData)

    override fun delete(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    ) = delete(
        keyValue(
            userId,
            sessionId,
        ),
    )

    override fun deleteAllOfUser(userId: IxId<UserData>) = delete("$userId:*")
}
