package app.index.data.sources.cache.cm.lists.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryDto
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto
import app.index.data.sources.cache.cm.lists.CategoryCM
import app.index.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [CategoryCM::class])
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
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ) = "$userId:$listId"

    override fun getAll(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): List<CategoryDto> = getAll(keyValue(userId, listId))

    override fun get(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryId: IxId<CategoryDto>,
    ): CategoryDto? =
        get(
            keyValue(userId, listId),
            categoryId.toString(),
        )

    override fun cacheAll(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoriesDto: List<CategoryDto>,
    ) {
        cacheAll(keyValue(userId, listId), categoriesDto.associateBy { it.id.toString() })
    }

    override fun cache(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryDto: CategoryDto,
    ) {
        cache(keyValue(userId, listId), categoryDto.id.toString(), categoryDto)
    }

    override fun delete(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryId: IxId<CategoryDto>,
    ) {
        delete(keyValue(userId, listId), categoryId.toString())
    }

    override fun deleteMultiple(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryIds: List<IxId<CategoryDto>>,
    ) {
        deleteMultiple(keyValue(userId, listId), *categoryIds.map { it.toString() }.toTypedArray())
    }

    override fun deleteAllOfUser(userId: IxId<UserDto>) {
        deleteAll("${userId}_*")
    }

    override fun deleteAllOfList(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ) {
        deleteAll(keyValue(userId, listId))
    }
}
