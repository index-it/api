package app.index.data.daos.list

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ItemData
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.lists.ItemContentCM
import app.index.data.sources.db.dbi.list.ItemContentDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemContentDao(
    private val itemDao: ItemDao,
    private val itemContentDBI: ItemContentDBI,
    private val itemContentCM: ItemContentCM,
) {
    suspend fun create(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData? {
        if (!itemDao.exists(userId, itemId)) {
            return null
        }

        val itemContentData = ItemContentData(
            id = newIxId(),
            user_id = userId,
            item_id = itemId,
            content = "",
        )

        itemContentDBI.create(itemContentData)
        itemContentCM.cache(userId, itemContentData)

        return itemContentData
    }

    suspend fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData? {
        var content = itemContentCM.get(userId, itemId)

        if (content == null) {
            content = itemContentDBI.get(userId, itemId)
                ?: return null
            itemContentCM.cache(userId, content)
        }

        return content
    }

    suspend fun getOrCreate(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) = get(userId, itemId) ?: create(userId, itemId)

    suspend fun update(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        itemContentCreateOrUpdateRequestData: ItemContentData.ItemContentCreateOrUpdateRequestData,
    ): ItemContentData? {
        val updated = itemContentDBI.update(userId, itemId, itemContentCreateOrUpdateRequestData)

        if (updated) {
            itemContentCM.delete(userId, itemId)
        }

        return get(userId, itemId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) {
        itemContentCM.delete(userId, itemId)
        itemContentDBI.delete(userId, itemId)
    }
}
