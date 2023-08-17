package app.index_it.core.cache

import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object CategoryCM: DoubleHashedCM("categories") {
    private fun keyValue(userId: Id<UserDto>, listId: Id<ListDto>) = "${userId}:${listId}"

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<CategoryDto> = getAll(keyValue(userId, listId))

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): CategoryDto? = get(keyValue(userId, listId), categoryId.toString())

    fun cacheAll(userId: Id<UserDto>, listId: Id<ListDto>, categoriesDto: List<CategoryDto>) {
        cacheAll(keyValue(userId, listId), categoriesDto.associateBy { it.id.toString() })
    }

    fun cache(userId: Id<UserDto>, listId: Id<ListDto>, categorieDto: CategoryDto) {
        cache(keyValue(userId, listId), categorieDto.id.toString(), CategoryDto)
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>) {
        delete(keyValue(userId, listId), categoryId.toString())
    }

    @Suppress("UNUSED")
    fun deleteMultiple(userId: Id<UserDto>, listId: Id<ListDto>, categoryIds: List<Id<CategoryDto>>) {
        deleteMultiple(keyValue(userId, listId), *categoryIds.map { it.toString() }.toTypedArray())
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        deleteAll("${userId}_*")
    }

    fun deleteAllOfList(userId: Id<UserDto>, listId: Id<ListDto>) {
        deleteAll(keyValue(userId, listId))
    }
}