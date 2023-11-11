package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.sources.db.dbi.list.CategoryDBI
import app.index_it.data.sources.db.schemas.lists.CategoryEntity
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object CategoryDBIImpl : CategoryDBI {
    private fun CategoryEntity.fromDto(categoryDto: CategoryDto) {
        list = categoryDto.listId.toEntityId(ListTable)
        name = categoryDto.name
        color = categoryDto.color
    }

    private fun CategoryEntity.toDto() = CategoryDto(
        id = id.toIxId(),
        listId = list.toIxId(),
        name = name,
        color = color
    )

    override suspend fun create(categoryDto: CategoryDto) {
        dbQuery {
            CategoryEntity.new(categoryDto.id.id) {
                fromDto(categoryDto)
            }
        }
    }

    override suspend fun get(id: IxId<CategoryDto>): CategoryDto? = dbQuery {
        CategoryEntity.findById(id.id)?.toDto()
    }

    override suspend fun getOfList(listId: IxId<ListDto>): List<CategoryDto> = dbQuery {
        CategoryEntity
            .find { CategoryTable.list eq listId.toEntityId(ListTable) }
            .map { it.toDto() }
    }

    override suspend fun update(
        categoryId: IxId<CategoryDto>,
        categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto
    ) {
        dbQuery {
            CategoryTable.update({ CategoryTable.id eq categoryId.toEntityId(CategoryTable) }) {
                it[name] = categoryUpdateRequestDto.name
                it[color] = categoryUpdateRequestDto.color
            }
        }
    }

    override suspend fun delete(id: IxId<CategoryDto>) {
        dbQuery {
            CategoryTable.deleteWhere { CategoryTable.id eq id.toEntityId(CategoryTable) }
        }
    }

}