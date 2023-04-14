package app.index_it.daos.list

import app.index_it.core.cache.ItemCM
import app.index_it.core.db.ItemDBM
import app.index_it.core.extentions.toObjectId
import app.index_it.models.lists.CategoryDto
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
                ItemCM.cacheAll(userId, listId, items)
        }

        return items
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>): ItemDto? {
        var item = ItemCM.get(userId, listId, itemId)

        if (item == null) {
            item = ItemDBM.get(userId, listId, itemId)
                ?: return null
            ItemCM.cache(userId, listId, item)
        }

        return item
    }

    fun getAllOfCategory(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = ItemCM.getAll(userId, listId).filter { it.categoryId == categoryId }

        if (items.isEmpty()) {
            items = ItemDBM.getAllOfCategory(userId, listId, categoryId)
            if (items.isNotEmpty())
                ItemCM.deleteAllOfList(userId, listId)
        }

        return items
    }

    fun create(userId: Id<UserDto>, listId: Id<ListDto>, itemCreateRequestDto: ItemDto.ItemCreateRequestDto): ItemDto {
        val itemDto = ItemDto(
            userId = userId,
            listId = listId,
            categoryId = itemCreateRequestDto.categoryId.toObjectId(),
            name = itemCreateRequestDto.name
        )

        ItemDBM.create(itemDto)
        ItemCM.cache(userId, listId, itemDto)

        return itemDto
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val item = ItemDBM.update(userId, listId, itemId, itemUpdateRequestDto)

        if (item != null)
            ItemCM.cache(userId, listId, item)
        else
            ItemCM.delete(userId, listId, itemId)

        return item
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        ItemDBM.delete(userId, listId, itemId)
        ItemCM.delete(userId, listId, itemId)
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        ItemDBM.deleteAllOfUser(userId)
        ItemCM.deleteAllOfUser(userId)
    }

    fun deleteAllOfList(userId: Id<UserDto>, listId: Id<ListDto>) {
        ItemDBM.deleteAllOfList(userId, listId)
        ItemCM.deleteAllOfList(userId, listId)
    }

    fun deleteAllOfCategory(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>) {
        val itemsOfCategory = getAllOfCategory(userId, listId, categoryId)
        ItemDBM.deleteAllOfCategory(userId, listId, categoryId)
        ItemCM.deleteMultiple(userId, listId, itemsOfCategory.map { it.id })
    }
}
