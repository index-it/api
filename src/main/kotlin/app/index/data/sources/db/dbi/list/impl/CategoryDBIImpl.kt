package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.sources.db.dbi.list.CategoryDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CategoryDBIImpl : CategoryDBI {
    private fun categoryFilter(categoryId: IxId<CategoryData>) = Op.build {
        CategoryTable.id eq categoryId.toEntityId(CategoryTable)
    }

    override suspend fun create(categoryData: CategoryData) {
        dbQuery {
            CategoryEntity.new(categoryData.id.id) {
                fromData(categoryData)
            }
        }
    }

    override suspend fun get(categoryId: IxId<CategoryData>): CategoryData? =
        dbQuery {
            CategoryEntity.find { categoryFilter(categoryId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun get(categoryIds: List<IxId<CategoryData>>): List<CategoryData> =
        dbQuery {
            CategoryEntity.find { CategoryTable.id inList categoryIds.map { it.toEntityId(CategoryTable) } }
                .map { it.toData() }
        }

    override suspend fun getOfList(listId: IxId<ListData>): List<CategoryData> =
        dbQuery {
            CategoryEntity
                .find {
                    CategoryTable.list eq listId.toEntityId(ListTable)
                }
                .map { it.toData() }
        }

    override suspend fun update(
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): CategoryData? =
        dbQuery {
            CategoryTable.updateReturning(where = { categoryFilter(categoryId) }) {
                it[name] = categoryUpdateRequestData.name
                it[color] = categoryUpdateRequestData.color
                it[edited_at] = DatetimeUtils.currentJavaInstant()
            }.firstOrNull()?.let {
                CategoryEntity.wrapRow(it).toData()
            }
        }

    override suspend fun delete(categoryId: IxId<CategoryData>): Boolean = dbQuery {
        CategoryTable.deleteWhere { categoryFilter(categoryId) } > 0
    }
}
