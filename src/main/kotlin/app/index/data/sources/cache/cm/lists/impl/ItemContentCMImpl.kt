package app.index.data.sources.cache.cm.lists.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemContentDto
import app.index.data.models.lists.ItemDto
import app.index.data.models.user.UserDto
import app.index.data.sources.cache.cm.lists.ItemContentCM
import app.index.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [ItemContentCM::class])
class ItemContentCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : ItemContentCM,
    DoubleHashedCM(
        keyBase = "item-contents",
        redisClient,
        objectMapper,
    ) {
    override fun get(
        userId: IxId<UserDto>,
        itemId: IxId<ItemDto>,
    ): ItemContentDto? = get(userId.toString(), itemId.toString())

    override fun cache(
        userId: IxId<UserDto>,
        itemContentDto: ItemContentDto,
    ) {
        cache(userId.toString(), itemContentDto.id.toString(), itemContentDto)
    }

    override fun delete(
        userId: IxId<UserDto>,
        itemId: IxId<ItemDto>,
    ) {
        delete(userId.toString(), itemId.toString())
    }

    override fun deleteMultiple(
        userId: IxId<UserDto>,
        itemIds: List<IxId<ItemDto>>,
    ) {
        deleteMultiple(userId.toString(), *itemIds.map { it.toString() }.toTypedArray())
    }

    override fun deleteAllOfUser(userId: IxId<UserDto>) {
        deleteAll("${userId}_*")
    }
}
