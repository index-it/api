package app.index_it.data.daos.list

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.db.dbi.list.ItemDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemDao(
    private val itemDBI: ItemDBI,
    private val itemCM: ItemCM,
    private val itemContentCM: ItemContentCM
) {
    suspend fun exists(userId: IxId<UserDto>, itemId: IxId<ItemDto>): Boolean {
        return itemDBI.exists(userId, itemId)
    }

    suspend fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = itemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = itemDBI.getOfList(userId, listId)
            if (items.isNotEmpty())
                itemCM.cacheAll(userId, listId, items)
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
        var item = itemCM.get(userId, listId, itemId)

        if (item == null) {
            item = itemDBI.get(userId, itemId)
                ?: return null
            itemCM.cache(userId, listId, item)
        }

        return item
    }

    suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemDto? {
        // Might use also cache with * query for the listId
        return itemDBI.get(userId, itemId)
    }

    suspend fun getAllOfCategory(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): List<ItemDto> {
        // TODO: Query db instead?
        var items = itemCM.getAll(userId, listId).filter { it.categoryId == categoryId }

        if (items.isEmpty()) {
            items = itemDBI.getOfCategory(userId, categoryId)
            if (items.isNotEmpty())
                itemCM.cacheAll(userId, listId, items)
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
            createdAt = DatetimeUtils.currentMillis(),
            editedAt = null,
            completedAt = null
        )

        itemDBI.create(itemDto)
        itemCM.cache(userId, listId, itemDto)

        return itemDto
    }

    suspend fun setCompletion(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, completed: Boolean): ItemDto? {
        val updated = itemDBI.setCompletion(userId, itemId, completed)

        if (updated) {
            itemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun setTaskConnection(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?): ItemDto? {
        val updated = itemDBI.setTaskConnection(userId, itemId, taskId)

        if (updated) {
            itemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val updated = itemDBI.update(userId, itemId, itemUpdateRequestDto)

        if (updated) {
            itemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>) {
        itemDBI.delete(userId, itemId)
        itemCM.delete(userId, listId, itemId)

        itemContentCM.delete(userId, itemId)
    }
}