package app.index_it.core.cache

import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ItemCM: DoubleHashedCM("items") {
    private fun keyValue(userId: Id<UserDto>, listId: Id<ListDto>) = "${userId}_${listId}"

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<ItemDto> = ItemCM.getAllValues(keyValue(userId, listId))

    fun createAll(userId: Id<UserDto>, listId: Id<ListDto>, itemsDto: List<ItemDto>) {
        ItemCM.cacheAllValues(keyValue(userId, listId), itemsDto.associateBy { it.id.toString() })
    }

    fun create(userId: Id<UserDto>, listId: Id<ListDto>, itemDto: ItemDto) {
        ItemCM.cacheValue(keyValue(userId, listId), itemDto.id.toString(), itemDto)
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, itemDto: ItemDto) {
        ItemCM.cacheValue(keyValue(userId, listId), itemDto.id.toString(), itemDto)
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        ItemCM.uncacheValue(keyValue(userId, listId), itemId.toString())
    }
}
