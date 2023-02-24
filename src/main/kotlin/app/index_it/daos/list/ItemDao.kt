package app.index_it.daos.list

import app.index_it.core.cache.ItemCM
import app.index_it.core.db.ItemDBM
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ItemDao {
    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = ItemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = ItemDBM.getAll(userId, listId)
            if (items.isNotEmpty())
                ItemCM.createAll(userId, listId, items)
        }

        return items
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>): ItemDto? {
        var item = ItemCM.get(userId, listId, itemId)

        if (item == null) {
            item = ItemDBM.get(userId, listId, itemId)
                ?: return null
            ItemCM.create(userId, listId, item)
        }

        return item
    }

    fun create(userId: Id<UserDto>, listId: Id<ListDto>, itemCreateRequestDto: ItemDto.ItemCreateRequestDto): ItemDto {
        val itemDto = ItemDto(
            user_id = userId,
            list_id = listId,
            category_id = itemCreateRequestDto.category_id,
            name = itemCreateRequestDto.name
        )

        ItemDBM.create(itemDto)
        ItemCM.create(userId, listId, itemDto)

        return itemDto
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val item = ItemDBM.update(userId, listId, itemId, itemUpdateRequestDto)

        if (item != null)
            ItemCM.update(userId, listId, item)
        else
            ItemCM.delete(userId, listId, itemId)

        return item
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        ItemDBM.delete(userId, listId, itemId)
        ItemCM.delete(userId, listId, itemId)
    }
}
