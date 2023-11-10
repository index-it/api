package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto

object CategoryCM: app.index_it.data.sources.cache.core.DoubleHashedCM("categories") {
    private fun keyValue(userId: IxId<UserDto>, listId: IxId<ListDto>) = "${userId}:${listId}"

    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<CategoryDto> = getAll(keyValue(userId, listId))

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): CategoryDto? = get(keyValue(userId, listId), categoryId.toString())

    fun cacheAll(userId: IxId<UserDto>, listId: IxId<ListDto>, categoriesDto: List<CategoryDto>) {
        cacheAll(keyValue(userId, listId), categoriesDto.associateBy { it.id.toString() })
    }

    fun cache(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryDto: CategoryDto) {
        cache(keyValue(userId, listId), categoryDto.id.toString(), categoryDto)
    }

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>) {
        delete(keyValue(userId, listId), categoryId.toString())
    }

    @Suppress("UNUSED")
    fun deleteMultiple(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryIds: List<Id<CategoryDto>>) {
        deleteMultiple(keyValue(userId, listId), *categoryIds.map { it.toString() }.toTypedArray())
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        deleteAll("${userId}_*")
    }

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        deleteAll(keyValue(userId, listId))
    }
}