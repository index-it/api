package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryDto
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.list.CategoryDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CategoryDBIImpl : CategoryDBI {
    private fun userAndCategoryFilter(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
    ) = Op.build { (CategoryTable.id eq categoryId.toEntityId(CategoryTable)) and (CategoryTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(categoryDto: CategoryDto) {
        dbQuery {
            CategoryEntity.new(categoryDto.id.id) {
                fromDto(categoryDto)
            }
        }
    }

    override suspend fun get(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
    ): CategoryDto? =
        dbQuery {
            CategoryEntity.find { userAndCategoryFilter(userId, categoryId) }
                .limit(1)
                .firstOrNull()
                ?.toDto()
        }

    override suspend fun getOfList(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): List<CategoryDto> =
        dbQuery {
            CategoryEntity
                .find {
                    (CategoryTable.list eq listId.toEntityId(ListTable)) and (CategoryTable.user eq userId.toEntityId(UsersTable))
                }
                .map { it.toDto() }
        }

    override suspend fun update(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
        categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto,
    ): Boolean =
        dbQuery {
            CategoryTable.update({ userAndCategoryFilter(userId, categoryId) }) {
                it[name] = categoryUpdateRequestDto.name
                it[color] = categoryUpdateRequestDto.color
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserDto>,
        categoryId: IxId<CategoryDto>,
    ) {
        dbQuery {
            CategoryTable.deleteWhere { userAndCategoryFilter(userId, categoryId) }
        }
    }
}
