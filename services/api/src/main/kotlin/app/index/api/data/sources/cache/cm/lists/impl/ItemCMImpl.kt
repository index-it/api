package app.index.api.data.sources.cache.cm.lists.impl

import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.cache.cm.lists.ItemCM
import app.index.api.data.sources.cache.core.DoubleHashedCM

@Suppress("DEPRECATION", "UNUSED")
class ItemCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : ItemCM,
    DoubleHashedCM(
        keyBase = "items",
        redisClient,
        objectMapper,
    ) {
    private fun keyValue(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) = "$userId:$listId"

    override fun getAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<ItemData> = getAll(keyValue(userId, listId))

    override fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
    ): ItemData? =
        get(
            keyValue(userId, listId),
            itemId.toString(),
        )

    override fun cacheAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemsDto: List<ItemData>,
    ) {
        cacheAll(keyValue(userId, listId), itemsDto.associateBy { it.id.toString() })
    }

    override fun cache(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemData: ItemData,
    ) {
        cache(keyValue(userId, listId), itemData.id.toString(), itemData)
    }

    override fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemId: IxId<ItemData>,
    ) {
        delete(keyValue(userId, listId), itemId.toString())
    }

    override fun deleteMultiple(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        itemIds: List<IxId<ItemData>>,
    ) {
        deleteMultiple(keyValue(userId, listId), *itemIds.map { it.toString() }.toTypedArray())
    }

    override fun deleteAllOfUser(userId: IxId<UserData>) {
        deleteAll("${userId}_*")
    }

    override fun deleteAllOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) {
        deleteAll(keyValue(userId, listId))
    }
}
