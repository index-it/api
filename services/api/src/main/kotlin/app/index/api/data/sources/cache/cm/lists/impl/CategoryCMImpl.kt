package app.index.api.data.sources.cache.cm.lists.impl

import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.CategoryData
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.cache.cm.lists.CategoryCM
import app.index.api.data.sources.cache.core.DoubleHashedCM

@Suppress("DEPRECATION", "UNUSED")
class CategoryCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : CategoryCM,
    DoubleHashedCM(
        keyBase = "categories",
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
    ): List<CategoryData> = getAll(keyValue(userId, listId))

    override fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
    ): CategoryData? =
        get(
            keyValue(userId, listId),
            categoryId.toString(),
        )

    override fun cacheAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoriesDto: List<CategoryData>,
    ) {
        cacheAll(keyValue(userId, listId), categoriesDto.associateBy { it.id.toString() })
    }

    override fun cache(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryData: CategoryData,
    ) {
        cache(keyValue(userId, listId), categoryData.id.toString(), categoryData)
    }

    override fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
    ) {
        delete(keyValue(userId, listId), categoryId.toString())
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
