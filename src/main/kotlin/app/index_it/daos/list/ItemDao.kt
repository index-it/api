package app.index_it.daos.list

import app.index_it.core.cache.ItemCM
import app.index_it.core.db.ItemDBM
import app.index_it.models.lists.ClientItemDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ItemDao {
    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<ItemDto> {
        var items = ItemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = ItemDBM.getAll(userId, listId)
            if (items.isNotEmpty())
                ItemCM.createAll(userId, listId, items)
        }

        return items
    }

    fun create(userId: Id<UserDto>, listId: Id<ListDto>, clientItemDto: ClientItemDto): ItemDto {
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

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, clientItemDto: ClientItemDto): ItemDto? {
        return ItemDBM.update(userId, listId, itemId, clientItemDto)?.let {
            ItemCM.update(userId, listId, it)
            it
        } ?: run {
            ItemCM.delete(userId, listId, itemId)
            null
        }
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        ItemDBM.delete(userId, listId, itemId)
        ItemCM.delete(userId, listId, itemId)
    }
}
