package app.index_it.data.daos.list

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.mongo.lists.ItemDBM

object ItemDao {
    fun exists(userId: IxId<UserDto>, itemId: IxId<ItemDto>): Boolean {
        return ItemDBM.exists(userId, itemId)
    }
    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = ItemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = ItemDBM.getAll(userId, listId)
            if (items.isNotEmpty())
                ItemCM.cacheAll(userId, listId, items)
        }

        return items
    }

    fun getAllUncompleted(userId: IxId<UserDto>, listId: IxId<ListDto>) =
        getAll(userId, listId)
            .filter { !it.completed }

    fun getAllCompleted(userId: IxId<UserDto>, listId: IxId<ListDto>) =
        getAll(userId, listId)
            .filter { it.completed }

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>): ItemDto? {
        var item = ItemCM.get(userId, listId, itemId)

        if (item == null) {
            item = ItemDBM.get(userId, listId, itemId)
                ?: return null
            ItemCM.cache(userId, listId, item)
        }

        return item
    }

    fun getAllOfCategory(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = ItemCM.getAll(userId, listId).filter { it.categoryId == categoryId }

        if (items.isEmpty()) {
            items = ItemDBM.getAllOfCategory(userId, listId, categoryId)
            if (items.isNotEmpty())
                ItemCM.cacheAll(userId, listId, items)
        }

        return items
    }

    fun create(userId: IxId<UserDto>, listId: IxId<ListDto>, itemCreateRequestDto: ItemDto.ItemCreateRequestDto): ItemDto {
        val itemDto = ItemDto(
            userId = userId,
            listId = listId,
            categoryId = itemCreateRequestDto.categoryId,
            taskId = null,
            name = itemCreateRequestDto.name,
            completed = false,
            createdAt = currentMillis(),
            editedAt = null,
            completedAt = null
        )

        ItemDBM.create(itemDto)
        ItemCM.cache(userId, listId, itemDto)

        return itemDto
    }

    fun setCompletion(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, completed: Boolean): ItemDto? {
        val item = ItemDBM.setCompletion(userId, listId, itemId, completed)

        if (item != null)
            ItemCM.cache(userId, listId, item)
        else
            ItemCM.delete(userId, listId, itemId)

        return item
    }

    fun setLinking(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?): ItemDto? {
        val item = ItemDBM.setLinking(userId, listId, itemId, taskId)

        if (item != null)
            ItemCM.cache(userId, listId, item)
        else
            ItemCM.delete(userId, listId, itemId)

        return item
    }

    fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val item = ItemDBM.update(userId, listId, itemId, itemUpdateRequestDto)

        if (item != null)
            ItemCM.cache(userId, listId, item)
        else
            ItemCM.delete(userId, listId, itemId)

        return item
    }

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>) {
        ItemDBM.delete(userId, listId, itemId)
        ItemCM.delete(userId, listId, itemId)
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        ItemDBM.deleteAllOfUser(userId)
        ItemCM.deleteAllOfUser(userId)
    }

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        ItemDBM.deleteAllOfList(userId, listId)
        ItemCM.deleteAllOfList(userId, listId)
    }

    fun deleteAllOfCategory(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>) {
        val itemsOfCategory = getAllOfCategory(userId, listId, categoryId)
        ItemDBM.deleteAllOfCategory(userId, listId, categoryId)
        ItemCM.deleteMultiple(userId, listId, itemsOfCategory.map { it.id })
    }
}
