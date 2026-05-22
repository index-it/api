package app.index.api.data.sources.cache.cm.lists

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.user.UserData

@Deprecated("Caching in front of database entities is not recommended anymore")
interface ItemCM {
    fun getAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<ItemData>

    fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
    ): ItemData?

    fun cacheAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemsDto: List<ItemData>,
    )

    fun cache(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemData: ItemData,
    )

    fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
    )

    fun deleteMultiple(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemIds: List<IxId<ItemData>>,
    )

    fun deleteAllOfUser(userId: IxId<UserData>)

    fun deleteAllOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    )
}
