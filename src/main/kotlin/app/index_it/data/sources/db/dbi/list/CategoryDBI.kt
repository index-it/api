package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.sources.db.dbi.DBI
import org.jetbrains.exposed.sql.Database

interface CategoryDBI : DBI {
    suspend fun create(categoryDto: CategoryDto)
    suspend fun get(categoryId: IxId<CategoryDto>): CategoryDto?
    suspend fun getOfList(listId: IxId<ListDto>): List<CategoryDto>
    suspend fun update(categoryId: IxId<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): CategoryDto?
    suspend fun delete(categoryId: IxId<CategoryDto>)
}