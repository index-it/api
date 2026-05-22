package app.index.shared.core.data.daos.list

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.typedId.newIxId
import app.index.shared.core.data.models.lists.ItemContentData
import app.index.shared.core.data.models.lists.ItemData
import app.index.shared.core.data.models.user.UserData
import app.index.shared.core.data.sources.db.dbi.list.ItemContentDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemContentDao(
    private val itemDao: ItemDao,
    private val itemContentDBI: ItemContentDBI,
) {
    suspend fun create(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData? {
        if (!itemDao.exists(itemId)) {
            return null
        }

        val itemContentData = ItemContentData(
            id = newIxId(),
            user_id = userId,
            item_id = itemId,
            content = "",
        )

        itemContentDBI.create(itemContentData)

        return itemContentData
    }

    suspend fun get(itemId: IxId<ItemData>): ItemContentData? {
        return itemContentDBI.get(itemId)
    }

    suspend fun getOrCreate(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) = get(itemId) ?: create(userId, itemId)

    suspend fun update(
        itemId: IxId<ItemData>,
        itemContentCreateOrUpdateRequestData: ItemContentData.ItemContentCreateOrUpdateRequestData,
    ): ItemContentData? {
        return itemContentDBI.update(itemId, itemContentCreateOrUpdateRequestData)
    }

    suspend fun delete(itemId: IxId<ItemData>) {
        itemContentDBI.delete(itemId)
    }
}
