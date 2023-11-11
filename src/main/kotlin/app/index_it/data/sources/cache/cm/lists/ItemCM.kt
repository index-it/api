package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.core.DoubleHashedCM

object ItemCM: DoubleHashedCM("items") {
    private fun keyValue(userId: IxId<UserDto>, listId: IxId<ListDto>) = "${userId}:${listId}"

    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> = getAll(keyValue(userId, listId))

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>): ItemDto? = get(keyValue(userId, listId), itemId.toString())

    fun cacheAll(userId: IxId<UserDto>, listId: IxId<ListDto>, itemsDto: List<ItemDto>) {
        cacheAll(keyValue(userId, listId), itemsDto.associateBy { it.id.toString() })
    }

    fun cache(userId: IxId<UserDto>, listId: IxId<ListDto>, itemDto: ItemDto) {
        cache(keyValue(userId, listId), itemDto.id.toString(), itemDto)
    }

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>) {
        delete(keyValue(userId, listId), itemId.toString())
    }

    fun deleteMultiple(userId: IxId<UserDto>, listId: IxId<ListDto>, itemIds: List<IxId<ItemDto>>) {
        deleteMultiple(keyValue(userId, listId), *itemIds.map { it.toString() }.toTypedArray())
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        deleteAll("${userId}_*")
    }

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        deleteAll(keyValue(userId, listId))
    }
}
