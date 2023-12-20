package app.index_it.data.sources.cache.cm.users.impl

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.users.UserCM
import app.index_it.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [UserCM::class])
class UserCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper
) : UserCM,
    HashedCM(
    keyName = "users",
    redisClient,
    objectMapper
) {
    override fun cache(userDto: UserDto) = cache(userDto.id.toString(), userDto)

    override fun get(id: IxId<UserDto>) : UserDto? = get(id.toString())

    override fun delete(id: IxId<UserDto>) = delete(id.toString())
}
