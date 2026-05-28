package app.index.shared.core.data.sources.cache.cm.users.impl

import app.index.shared.core.config.ApiConfig
import app.index.shared.core.clients.RedisClient
import app.index.shared.core.logic.ObjectMapper
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [_root_ide_package_.app.index.shared.core.data.sources.cache.cm.users.UserSessionCM::class])
class UserSessionCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : app.index.shared.core.data.sources.cache.cm.users.UserSessionCM,
    app.index.shared.core.data.sources.cache.core.ExpiringCM(
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
