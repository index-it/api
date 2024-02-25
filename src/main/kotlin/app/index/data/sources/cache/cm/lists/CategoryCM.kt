package app.index.data.sources.cache.cm.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData

@Deprecated("Caching in front of database entities is not recommended anymore")
interface CategoryCM {
    fun getAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<CategoryData>

    fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
    ): CategoryData?

    fun cacheAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoriesDto: List<CategoryData>,
    )

    fun cache(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryData: CategoryData,
    )

    fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
    )
    
    fun deleteAllOfUser(userId: IxId<UserData>)

    fun deleteAllOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    )
}
