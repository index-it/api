package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
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
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ) = Op.build { (CategoryTable.id eq categoryId.toEntityId(CategoryTable)) and (CategoryTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(categoryData: CategoryData) {
        dbQuery {
            CategoryEntity.new(categoryData.id.id) {
                fromDto(categoryData)
            }
        }
    }

    override suspend fun get(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ): CategoryData? =
        dbQuery {
            CategoryEntity.find { userAndCategoryFilter(userId, categoryId) }
                .limit(1)
                .firstOrNull()
                ?.toDto()
        }

    override suspend fun getOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<CategoryData> =
        dbQuery {
            CategoryEntity
                .find {
                    (CategoryTable.list eq listId.toEntityId(ListTable)) and (CategoryTable.user eq userId.toEntityId(UsersTable))
                }
                .map { it.toDto() }
        }

    override suspend fun update(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): Boolean =
        dbQuery {
            CategoryTable.update({ userAndCategoryFilter(userId, categoryId) }) {
                it[name] = categoryUpdateRequestData.name
                it[color] = categoryUpdateRequestData.color
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ) {
        dbQuery {
            CategoryTable.deleteWhere { userAndCategoryFilter(userId, categoryId) }
        }
    }
}
