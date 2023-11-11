package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.list.CategoryDBI
import app.index_it.data.sources.db.schemas.lists.CategoryEntity
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object CategoryDBIImpl : CategoryDBI {
    private fun CategoryEntity.fromDto(categoryDto: CategoryDto) {
        user = categoryDto.userId.toEntityId(UserTable)
        list = categoryDto.listId.toEntityId(ListTable)
        name = categoryDto.name
        color = categoryDto.color
    }

    private fun CategoryEntity.toDto() = CategoryDto(
        id = id.toIxId(),
        userId = user.toIxId(),
        listId = list.toIxId(),
        name = name,
        color = color
    )

    private fun userAndCategoryFilter(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>) = Op.build { (CategoryTable.id eq categoryId.toEntityId(CategoryTable)) and (CategoryTable.user eq userId.toEntityId(UserTable)) }

    override suspend fun create(categoryDto: CategoryDto) {
        dbQuery {
            CategoryEntity.new(categoryDto.id.id) {
                fromDto(categoryDto)
            }
        }
    }

    override suspend fun get(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>): CategoryDto? = dbQuery {
        CategoryEntity.find { userAndCategoryFilter(userId, categoryId) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun getOfList(userId: IxId<UserDto>, listId: IxId<ListDto>): List<CategoryDto> = dbQuery {
        CategoryEntity
            .find {
                (CategoryTable.list eq listId.toEntityId(ListTable)) and (CategoryTable.user eq userId.toEntityId(UserTable))
            }
            .map { it.toDto() }
    }

    override suspend fun update(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
        categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto
    ): Boolean = dbQuery {
        CategoryTable.update({ userAndCategoryFilter(userId, categoryId)} ) {
            it[name] = categoryUpdateRequestDto.name
            it[color] = categoryUpdateRequestDto.color
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>) {
        dbQuery {
            CategoryTable.deleteWhere { userAndCategoryFilter(userId, categoryId) }
        }
    }

}