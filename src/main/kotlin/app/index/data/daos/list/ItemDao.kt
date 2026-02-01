package app.index.data.daos.list

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.ItemDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemDao(
    private val itemDBI: ItemDBI,
) {
    suspend fun exists(itemId: IxId<ItemData>): Boolean {
        return itemDBI.exists(itemId)
    }

    suspend fun getAll(listId: IxId<ListData>): List<ItemData> {
        return itemDBI.getOfList(listId)
    }

    suspend fun getAllUncompleted(listId: IxId<ListData>): List<ItemData> {
        return itemDBI.getUncompletedOfList(listId)
    }

    suspend fun getAllCompleted(listId: IxId<ListData>): List<ItemData> {
        return itemDBI.getCompletedOfList(listId)
    }

    suspend fun get(itemId: IxId<ItemData>): ItemData? {
        return itemDBI.get(itemId)
    }

    suspend fun get(itemIds: List<IxId<ItemData>>): List<ItemData> {
        return itemDBI.get(itemIds)
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
            name = itemCreateRequestData.name,
            link = itemCreateRequestData.link,
            note = itemCreateRequestData.note,
            completed = false,
            created_at = DatetimeUtils.currentMillis(),
            edited_at = null,
            completed_at = null,
        )

        itemDBI.create(itemData)

        return itemData
    }

    suspend fun setCompletion(
        itemId: IxId<ItemData>,
        completed: Boolean,
    ): ItemData? {
        return itemDBI.setCompletion(itemId, completed)
    }

    suspend fun setCompletion(
        itemIds: List<IxId<ItemData>>,
        completed: Boolean,
    ): List<ItemData> {
        return itemDBI.setCompletion(itemIds, completed)
    }

    suspend fun update(
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): ItemData? {
        return itemDBI.update(itemId, itemUpdateRequestData)
    }

    suspend fun move(
        data: ItemData.ItemsMoveRequestData,
    ): List<ItemData> {
        return itemDBI.move(data)
    }

    suspend fun delete(itemId: IxId<ItemData>): Boolean {
        val deleted = itemDBI.delete(itemId)

        return deleted
    }

    suspend fun delete(itemIds: List<IxId<ItemData>>): Boolean {
        val deleted = itemDBI.delete(itemIds)

        return deleted
    }
}
