package app.index_it.core.cache

import app.index_it.models.lists.ItemDto

object ItemCM: DoubleHashedCM("items") {
    private fun keyValue(userId: String, listId: String) = "${userId}_${listId}"

    fun getAll(userId: String, listId: String): List<ItemDto> = ItemCM.getAllValues(keyValue(userId, listId))

    fun createAll(userId: String, listId: String, itemsDto: List<ItemDto>) {
        ItemCM.cacheAllValues(keyValue(userId, listId), itemsDto.associateBy { it.id })
    }

    fun create(userId: String, listId: String, itemDto: ItemDto) {
        ItemCM.cacheValue(keyValue(userId, listId), itemDto.id, itemDto)
    }

    fun update(userId: String, listId: String, itemDto: ItemDto) {
        ItemCM.cacheValue(keyValue(userId, listId), itemDto.id, itemDto)
    }

    fun delete(userId: String, listId: String, itemId: String) {
        ItemCM.uncacheValue(keyValue(userId, listId), itemId)
    }
}
