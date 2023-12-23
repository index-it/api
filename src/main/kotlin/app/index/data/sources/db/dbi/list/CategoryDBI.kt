package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface CategoryDBI : DBI {
    suspend fun create(categoryData: CategoryData)

    suspend fun get(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ): CategoryData?

    suspend fun getOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<CategoryData>

    suspend fun update(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): Boolean

    /**
     * @return true for deleted, false if nothing was matched in the database
     */
    suspend fun delete(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ) : Boolean
}
