package app.index_it.data.sources.cache.cm.lists.impl

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [ItemCM::class])
class ItemCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper
): ItemCM,
    DoubleHashedCM(
    keyBase = "items",
    redisClient,
    objectMapper
) {
    private fun keyValue(userId: IxId<UserDto>, listId: IxId<ListDto>) = "${userId}:${listId}"

    override fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> = getAll(keyValue(userId, listId))

    override fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>): ItemDto? = get(keyValue(userId, listId), itemId.toString())

    override fun cacheAll(userId: IxId<UserDto>, listId: IxId<ListDto>, itemsDto: List<ItemDto>) {
        cacheAll(keyValue(userId, listId), itemsDto.associateBy { it.id.toString() })
    }

    override fun cache(userId: IxId<UserDto>, listId: IxId<ListDto>, itemDto: ItemDto) {
        cache(keyValue(userId, listId), itemDto.id.toString(), itemDto)
    }

    override fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>) {
        delete(keyValue(userId, listId), itemId.toString())
    }

    override fun deleteMultiple(userId: IxId<UserDto>, listId: IxId<ListDto>, itemIds: List<IxId<ItemDto>>) {
        deleteMultiple(keyValue(userId, listId), *itemIds.map { it.toString() }.toTypedArray())
    }

    override fun deleteAllOfUser(userId: IxId<UserDto>) {
        deleteAll("${userId}_*")
    }

    override fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        deleteAll(keyValue(userId, listId))
    }
}
