package app.index.data.sources.cache.cm.users.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.users.UserCM
import app.index.data.sources.cache.core.HashedCM
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
