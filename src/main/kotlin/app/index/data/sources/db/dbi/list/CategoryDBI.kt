package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.sources.db.dbi.DBI

interface CategoryDBI : DBI {
    suspend fun create(categoryData: CategoryData)

    suspend fun get(categoryId: IxId<CategoryData>): CategoryData?

    suspend fun getOfList(listId: IxId<ListData>): List<CategoryData>

    suspend fun update(
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): Boolean

    /**
     * @return true for deleted, false if nothing was matched in the database
     */
    suspend fun delete(categoryId: IxId<CategoryData>) : Boolean
}
