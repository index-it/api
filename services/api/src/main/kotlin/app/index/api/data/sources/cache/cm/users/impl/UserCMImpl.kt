package app.index.api.data.sources.cache.cm.users.impl

import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.cache.cm.users.UserCM
import app.index.api.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [UserCM::class])
class UserCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : UserCM,
    HashedCM(
        keyName = "users",
        redisClient,
        objectMapper,
    ) {
    override fun cache(userData: UserData) = cache(userData.id.toString(), userData)

    override fun get(id: IxId<UserData>): UserData? = get(id.toString())

    override fun delete(id: IxId<UserData>) = delete(id.toString())
}
