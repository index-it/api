package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ItemData
import app.index.data.sources.db.dbi.DBI

interface ItemContentDBI : DBI {
    suspend fun create(itemContentData: ItemContentData)

    suspend fun get(itemId: IxId<ItemData>): ItemContentData?

    /**
     * @return updated [ItemContentData] or null if nothing matched the [itemId]
     */
    suspend fun update(
        itemId: IxId<ItemData>,
        itemContentCreateOrUpdateRequestData: ItemContentData.ItemContentCreateOrUpdateRequestData,
    ): ItemContentData?

    /**
     * @return true for deleted, false if nothing matched the [itemId]
     */
    suspend fun delete(itemId: IxId<ItemData>): Boolean
}
