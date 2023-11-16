package app.index_it.data.daos.list

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.db.dbi.list.impl.ItemDBIImpl

object ItemDao {
    suspend fun exists(userId: IxId<UserDto>, itemId: IxId<ItemDto>): Boolean {
        return ItemDBIImpl.exists(userId, itemId)
    }

    suspend fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = ItemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = ItemDBIImpl.getOfList(userId, listId)
            if (items.isNotEmpty())
                ItemCM.cacheAll(userId, listId, items)
        }

        return items
    }

    suspend fun getAllUncompleted(userId: IxId<UserDto>, listId: IxId<ListDto>) =
        getAll(userId, listId)
            .filter { !it.completed }

    suspend fun getAllCompleted(userId: IxId<UserDto>, listId: IxId<ListDto>) =
        getAll(userId, listId)
            .filter { it.completed }

    suspend fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>): ItemDto? {
        var item = ItemCM.get(userId, listId, itemId)

        if (item == null) {
            item = ItemDBIImpl.get(userId, itemId)
                ?: return null
            ItemCM.cache(userId, listId, item)
        }

        return item
    }

    suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemDto? {
        // Might use also cache with * query for the listId
        return ItemDBIImpl.get(userId, itemId)
    }

    suspend fun getAllOfCategory(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = ItemCM.getAll(userId, listId).filter { it.categoryId == categoryId }

        if (items.isEmpty()) {
            items = ItemDBIImpl.getOfCategory(userId, categoryId)
            if (items.isNotEmpty())
                ItemCM.cacheAll(userId, listId, items)
        }

        return items
    }

    suspend fun create(userId: IxId<UserDto>, listId: IxId<ListDto>, itemCreateRequestDto: ItemDto.ItemCreateRequestDto): ItemDto {
        val itemDto = ItemDto(
            id = newIxId(),
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

        ItemDBIImpl.create(itemDto)
        ItemCM.cache(userId, listId, itemDto)

        return itemDto
    }

    suspend fun setCompletion(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, completed: Boolean): ItemDto? {
        val updated = ItemDBIImpl.setCompletion(userId, itemId, completed)

        if (updated) {
            ItemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun setTaskConnection(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?): ItemDto? {
        val updated = ItemDBIImpl.setTaskConnection(userId, itemId, taskId)

        return if (updated) {
            ItemCM.delete(userId, listId, itemId)
            get(userId, listId, itemId)
        } else {
            null
        }
    }

    suspend fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val updated = ItemDBIImpl.update(userId, itemId, itemUpdateRequestDto)

        return if (updated) {
            ItemCM.delete(userId, listId, itemId)
            get(userId, listId, itemId)
        } else {
            null
        }
    }

    suspend fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>) {
        ItemDBIImpl.delete(userId, itemId)
        ItemCM.delete(userId, listId, itemId)

        ItemContentCM.delete(userId, itemId)
    }

    /*

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
     */
}