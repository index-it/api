package app.index.shared.core.data.sources.cache.cm.users.impl

import app.index.shared.core.clients.RedisClient
import app.index.shared.core.logic.ObjectMapper
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.UserData
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [_root_ide_package_.app.index.shared.core.data.sources.cache.cm.users.UserCM::class])
class UserCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : app.index.shared.core.data.sources.cache.cm.users.UserCM,
    app.index.shared.core.data.sources.cache.core.HashedCM(
        keyName = "users",
        redisClient,
        objectMapper,
    ) {
    override fun cache(userData: UserData) = cache(userData.id.toString(), userData)

    override fun get(id: IxId<UserData>): UserData? = get(id.toString())

    override fun delete(id: IxId<UserData>) = delete(id.toString())
}
