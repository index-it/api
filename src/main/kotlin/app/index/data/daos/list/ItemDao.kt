package app.index.data.daos.list

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.lists.ItemCM
import app.index.data.sources.cache.cm.lists.ItemContentCM
import app.index.data.sources.db.dbi.list.ItemDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemDao(
    private val itemDBI: ItemDBI,
    private val itemCM: ItemCM,
    private val itemContentCM: ItemContentCM,
) {
    suspend fun exists(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): Boolean {
        return itemDBI.exists(userId, itemId)
    }

    suspend fun getAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<ItemData> {
        var items = itemCM.getAll(userId, listId)

        if (items.isEmpty()) {
            items = itemDBI.getOfList(userId, listId)
            if (items.isNotEmpty()) {
                itemCM.cacheAll(userId, listId, items)
            }
        }

        return items
    }

    suspend fun getAllUncompleted(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) = getAll(userId, listId)
        .filter { !it.completed }

    suspend fun getAllCompleted(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) = getAll(userId, listId)
        .filter { it.completed }

    suspend fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
    ): ItemData? {
        var item = itemCM.get(userId, listId, itemId)

        if (item == null) {
            item = itemDBI.get(userId, itemId)
                ?: return null
            itemCM.cache(userId, listId, item)
        }

        return item
    }

    suspend fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemData? {
        // Might use also cache with * query for the listId
        return itemDBI.get(userId, itemId)
    }

    suspend fun create(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemCreateRequestData: ItemData.ItemCreateRequestData,
    ): ItemData {
        val itemData = ItemData(
            id = newIxId(),
            user_id = userId,
            list_id = listId,
            category_id = itemCreateRequestData.category_id,
            task_id = null,
            name = itemCreateRequestData.name,
            link = itemCreateRequestData.link,
            completed = false,
            created_at = DatetimeUtils.currentMillis(),
            edited_at = null,
            completed_at = null,
        )

        itemDBI.create(itemData)
        itemCM.cache(userId, listId, itemData)

        return itemData
    }

    suspend fun setCompletion(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
        completed: Boolean,
    ): ItemData? {
        val updated = itemDBI.setCompletion(userId, itemId, completed)

        if (updated) {
            itemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun setTaskConnection(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
        taskId: IxId<TaskData>?,
    ): ItemData? {
        val updated = itemDBI.setTaskConnection(userId, itemId, taskId)

        if (updated) {
            itemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): ItemData? {
        val updated = itemDBI.update(userId, itemId, itemUpdateRequestData)

        if (updated) {
            itemCM.delete(userId, listId, itemId)
        }

        return get(userId, listId, itemId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
    ): Boolean {
        val deleted = itemDBI.delete(userId, itemId)
        itemCM.delete(userId, listId, itemId)

        itemContentCM.delete(userId, itemId)

        return deleted
    }
}
