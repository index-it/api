package app.index_it.data.sources.cache.cm.lists

import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import org.litote.kmongo.Id

object ItemCM: app.index_it.data.sources.cache.core.DoubleHashedCM("items") {
    private fun keyValue(userId: Id<UserDto>, listId: Id<ListDto>) = "${userId}:${listId}"

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<ItemDto> = getAll(keyValue(userId, listId))

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>): ItemDto? = get(keyValue(userId, listId), itemId.toString())

    fun cacheAll(userId: Id<UserDto>, listId: Id<ListDto>, itemsDto: List<ItemDto>) {
        cacheAll(keyValue(userId, listId), itemsDto.associateBy { it.id.toString() })
    }

    fun cache(userId: Id<UserDto>, listId: Id<ListDto>, itemDto: ItemDto) {
        cache(keyValue(userId, listId), itemDto.id.toString(), itemDto)
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        delete(keyValue(userId, listId), itemId.toString())
    }

    fun deleteMultiple(userId: Id<UserDto>, listId: Id<ListDto>, itemIds: List<Id<ItemDto>>) {
        deleteMultiple(keyValue(userId, listId), *itemIds.map { it.toString() }.toTypedArray())
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        deleteAll("${userId}_*")
    }

    fun deleteAllOfList(userId: Id<UserDto>, listId: Id<ListDto>) {
        deleteAll(keyValue(userId, listId))
    }
}
