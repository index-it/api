package app.index_it.daos

import app.index_it.core.cache.ItemCM
import app.index_it.core.db.ItemDBM
import app.index_it.models.lists.ClientItemDto
import app.index_it.models.lists.ItemDto

object ItemDao {
    fun getAll(userId: String, listId: String): List<ItemDto> {
        var items = ItemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = ItemDBM.getAll(userId, listId)
            if (items.isNotEmpty())
                ItemCM.createAll(userId, listId, items)
        }

        return items
    }

    fun create(userId: String, listId: String, clientItemDto: ClientItemDto): ItemDto {
        val itemDto = ItemDto(
            user_id = userId,
            list_id = listId,
            category_id = clientItemDto.category_id,
            name = clientItemDto.name
        )

        ItemDBM.create(itemDto)
        ItemCM.create(userId, listId, itemDto)

        return itemDto
    }

    fun update(userId: String, listId: String, itemId: String, clientItemDto: ClientItemDto): ItemDto? {
        return ItemDBM.update(userId, listId, itemId, clientItemDto)?.let {
            ItemCM.update(userId, listId, it)
            it
        } ?: run {
            ItemCM.delete(userId, listId, itemId)
            null
        }
    }

    fun delete(userId: String, listId: String, itemId: String) {
        ItemDBM.delete(userId, listId, itemId)
        ItemCM.delete(userId, listId, itemId)
    }
}
