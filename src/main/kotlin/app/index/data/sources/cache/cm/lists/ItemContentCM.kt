package app.index.data.sources.cache.cm.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ItemData
import app.index.data.models.user.UserData

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
