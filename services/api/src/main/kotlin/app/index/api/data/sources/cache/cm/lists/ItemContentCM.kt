package app.index.api.data.sources.cache.cm.lists

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ItemContentData
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.user.UserData

@Deprecated("Caching in front of database entities is not recommended anymore")
interface ItemContentCM {
    fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData?

    fun cache(
        userId: IxId<UserData>,
        itemContentData: ItemContentData,
    )

    fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    )

    fun deleteMultiple(
        userId: IxId<UserData>,
        itemIds: List<IxId<ItemData>>,
    )

    fun deleteAllOfUser(userId: IxId<UserData>)
}
