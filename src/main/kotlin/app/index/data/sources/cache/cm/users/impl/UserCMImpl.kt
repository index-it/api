package app.index.data.sources.cache.cm.users.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserDto
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
    override fun cache(userDto: UserDto) = cache(userDto.id.toString(), userDto)

    override fun get(id: IxId<UserDto>): UserDto? = get(id.toString())

    override fun delete(id: IxId<UserDto>) = delete(id.toString())
}
