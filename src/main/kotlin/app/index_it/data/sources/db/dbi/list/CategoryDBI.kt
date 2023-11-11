package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface CategoryDBI : DBI {
    suspend fun create(categoryDto: CategoryDto)
    suspend fun get(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>): CategoryDto?
    suspend fun getOfList(userId: IxId<UserDto>, listId: IxId<ListDto>): List<CategoryDto>
    suspend fun update(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): Boolean
    suspend fun delete(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>)
}