package app.index_it.data.sources.cache.cm.lists.impl

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ListCM
import app.index_it.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [ListCM::class])
class ListCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper
): ListCM,
    DoubleHashedCM(
    keyBase = "lists",
    redisClient,
    objectMapper
) {
    override fun getAll(userId: IxId<UserDto>): List<ListDto> = getAll(userId.toString())

    override fun get(userId: IxId<UserDto>, listId: IxId<ListDto>): ListDto? = get(userId.toString(), listId.toString())

    override fun cacheAll(userId: IxId<UserDto>, listsDto: List<ListDto>) {
        cacheAll(userId.toString(), listsDto.associateBy { it.id.toString() })
    }

    override fun cache(userId: IxId<UserDto>, listDto: ListDto) {
        cache(userId.toString(), listDto.id.toString(), listDto)
    }

    override fun update(userId: IxId<UserDto>, listDto: ListDto) {
        cache(userId.toString(), listDto.id.toString(), listDto)
    }

    override fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        delete(userId.toString(), listId.toString())
    }

    override fun deleteAll(userId: IxId<UserDto>) {
        deleteAll(userId.toString())
    }
}
