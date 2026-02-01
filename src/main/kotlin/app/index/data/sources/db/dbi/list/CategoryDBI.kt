package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.sources.db.dbi.DBI

interface CategoryDBI : DBI {
    suspend fun create(categoryData: CategoryData)

    suspend fun get(categoryId: IxId<CategoryData>): CategoryData?

    suspend fun get(categoryIds: List<IxId<CategoryData>>): List<CategoryData>

    suspend fun getOfList(listId: IxId<ListData>): List<CategoryData>

    /**
     * @return updated [CategoryData] or null if nothing matched the [categoryId]
     */
    suspend fun update(
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): CategoryData?

    /**
     * @return true for deleted, false if nothing matched the [categoryId]
     */
    suspend fun delete(categoryId: IxId<CategoryData>) : Boolean
}
