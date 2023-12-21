package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryDto
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.DBI

interface CategoryDBI : DBI {
    suspend fun create(categoryDto: CategoryDto)

    suspend fun get(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
    ): CategoryDto?

    suspend fun getOfList(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): List<CategoryDto>

    suspend fun update(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
        categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto,
    ): Boolean

    suspend fun delete(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
    )
}
