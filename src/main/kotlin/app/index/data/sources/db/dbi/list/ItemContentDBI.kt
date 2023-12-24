package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ItemData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface ItemContentDBI : DBI {
    suspend fun create(itemContentData: ItemContentData)

    suspend fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData?

    suspend fun update(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        itemContentCreateOrUpdateRequestData: ItemContentData.ItemContentCreateOrUpdateRequestData,
    ): Boolean

    suspend fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    )
}
