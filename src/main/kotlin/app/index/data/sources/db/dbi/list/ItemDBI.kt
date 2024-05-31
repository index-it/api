package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.sources.db.dbi.DBI

interface ItemDBI : DBI {
    suspend fun exists(
        itemId: IxId<ItemData>,
    ): Boolean

    suspend fun create(itemData: ItemData)

    suspend fun get(
        itemId: IxId<ItemData>,
    ): ItemData?

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
    ): Boolean

    suspend fun update(
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): Boolean

    suspend fun delete(
        itemId: IxId<ItemData>,
    ) : Boolean
}
