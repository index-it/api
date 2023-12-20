package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto

interface CategoryCM {
    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<CategoryDto>

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): CategoryDto?

    fun cacheAll(userId: IxId<UserDto>, listId: IxId<ListDto>, categoriesDto: List<CategoryDto>)
    fun cache(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryDto: CategoryDto)

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>)

    @Suppress("UNUSED")
    fun deleteMultiple(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryIds: List<IxId<CategoryDto>>)

    fun deleteAllOfUser(userId: IxId<UserDto>)

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>)
}