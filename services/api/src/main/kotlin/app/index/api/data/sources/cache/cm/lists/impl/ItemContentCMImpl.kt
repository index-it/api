package app.index.api.data.sources.cache.cm.lists.impl

import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ItemContentData
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.cache.cm.lists.ItemContentCM
import app.index.api.data.sources.cache.core.DoubleHashedCM

@Suppress("DEPRECATION", "UNUSED")
class ItemContentCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : ItemContentCM,
    DoubleHashedCM(
        keyBase = "item-contents",
        redisClient,
        objectMapper,
    ) {
    override fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData? = get(userId.toString(), itemId.toString())

    override fun cache(
        userId: IxId<UserData>,
        itemContentData: ItemContentData,
    ) {
        cache(userId.toString(), itemContentData.id.toString(), itemContentData)
    }

    override fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) {
        delete(userId.toString(), itemId.toString())
    }

    override fun deleteMultiple(
        userId: IxId<UserData>,
        itemIds: List<IxId<ItemData>>,
    ) {
        deleteMultiple(userId.toString(), *itemIds.map { it.toString() }.toTypedArray())
    }

    override fun deleteAllOfUser(userId: IxId<UserData>) {
        deleteAll("${userId}_*")
    }
}
