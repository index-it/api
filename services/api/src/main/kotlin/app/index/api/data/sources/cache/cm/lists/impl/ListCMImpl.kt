package app.index.api.data.sources.cache.cm.lists.impl

import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.cache.cm.lists.ListCM
import app.index.api.data.sources.cache.core.DoubleHashedCM

@Suppress("DEPRECATION", "UNUSED")
class ListCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : ListCM,
    DoubleHashedCM(
        keyBase = "lists",
        redisClient,
        objectMapper,
    ) {
    override fun getAll(userId: IxId<UserData>): List<ListData> = getAll(userId.toString())

    override fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): ListData? = get(userId.toString(), listId.toString())

    override fun cacheAll(
        userId: IxId<UserData>,
        listsDto: List<ListData>,
    ) {
        cacheAll(userId.toString(), listsDto.associateBy { it.id.toString() })
    }

    override fun cache(
        userId: IxId<UserData>,
        listData: ListData,
    ) {
        cache(userId.toString(), listData.id.toString(), listData)
    }

    override fun update(
        userId: IxId<UserData>,
        listData: ListData,
    ) {
        cache(userId.toString(), listData.id.toString(), listData)
    }

    override fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) {
        delete(userId.toString(), listId.toString())
    }

    override fun deleteAllOfUser(userId: IxId<UserData>) {
        deleteAll(userId.toString())
    }
}
