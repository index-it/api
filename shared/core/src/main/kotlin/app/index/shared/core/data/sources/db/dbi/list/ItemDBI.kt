package app.index.shared.core.data.sources.db.dbi.list

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.ItemData
import app.index.shared.core.data.models.lists.ListData
import app.index.shared.core.data.sources.db.dbi.DBI

interface ItemDBI : app.index.shared.core.data.sources.db.dbi.DBI {
    suspend fun exists(
        itemId: IxId<ItemData>,
    ): Boolean

    suspend fun create(itemData: ItemData)

    suspend fun get(
        itemId: IxId<ItemData>,
    ): ItemData?

    suspend fun get(
        itemIds: List<IxId<ItemData>>,
    ): List<ItemData>

    suspend fun getOfList(
        listId: IxId<ListData>,
    ): List<ItemData>

    suspend fun getUncompletedOfList(
        listId: IxId<ListData>
    ): List<ItemData>
    
    suspend fun getCompletedOfList(
        listId: IxId<ListData>
    ): List<ItemData>

    suspend fun setCompletion(
        itemId: IxId<ItemData>,
        completed: Boolean,
    ): ItemData?

    suspend fun setCompletion(
        itemIds: List<IxId<ItemData>>,
        completed: Boolean,
    ): List<ItemData>

    suspend fun update(
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): ItemData?

    suspend fun move(
        data: ItemData.ItemsMoveRequestData
    ): List<ItemData>

    suspend fun delete(
        itemId: IxId<ItemData>,
    ): Boolean

    suspend fun delete(
        itemIds: List<IxId<ItemData>>,
    ): Boolean
}
