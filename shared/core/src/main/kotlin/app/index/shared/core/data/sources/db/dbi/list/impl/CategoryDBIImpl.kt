package app.index.shared.core.data.sources.db.dbi.list.impl

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.CategoryData
import app.index.shared.core.data.models.lists.ListData
import app.index.shared.core.data.sources.db.dbi.list.CategoryDBI
import app.index.shared.core.data.sources.db.schemas.lists.*
import app.index.shared.core.data.sources.db.schemas.lists.fromData
import app.index.shared.core.data.sources.db.schemas.lists.toData
import app.index.shared.core.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CategoryDBIImpl : app.index.shared.core.data.sources.db.dbi.list.CategoryDBI {
    private fun categoryFilter(categoryId: IxId<CategoryData>) = Op.build {
        _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryTable.id eq categoryId.toEntityId(
            _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryTable
        )
    }

    override suspend fun create(categoryData: CategoryData) {
        dbQuery {
            _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryEntity.new(categoryData.id.id) {
                fromData(categoryData)
            }
        }
    }

    override suspend fun get(categoryId: IxId<CategoryData>): CategoryData? =
        dbQuery {
            _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryEntity.find { categoryFilter(categoryId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun get(categoryIds: List<IxId<CategoryData>>): List<CategoryData> =
        dbQuery {
            _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryEntity.find { _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryTable.id inList categoryIds.map { it.toEntityId(
                _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryTable
            ) } }
                .map { it.toData() }
        }

    override suspend fun getOfList(listId: IxId<ListData>): List<CategoryData> =
        dbQuery {
            _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryEntity
                .find {
                    _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.CategoryTable.list eq listId.toEntityId(
                        _root_ide_package_.app.index.shared.core.data.sources.db.schemas.lists.ListTable
                    )
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
