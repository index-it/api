package app.index.data.sources.cache.cm.lists.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.lists.ListCM
import app.index.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [ListCM::class])
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
