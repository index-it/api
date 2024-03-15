package app.index.data.daos.list

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.CategoryDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CategoryDao(
    private val categoryDBI: CategoryDBI,
) {
    suspend fun getAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<CategoryData> {
        return categoryDBI.getOfList(userId, listId)
    }

    suspend fun get(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ): CategoryData? {
        return categoryDBI.get(userId, categoryId)
    }

    suspend fun create(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryCreateRequestData: CategoryData.CategoryCreateRequestData,
    ): CategoryData {
        val categoryData = CategoryData(
            id = newIxId(),
            user_id = userId,
            list_id = listId,
            name = categoryCreateRequestData.name,
            color = categoryCreateRequestData.color,
        )

        categoryDBI.create(categoryData)

        return categoryData
    }

    suspend fun update(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): CategoryData? {
        categoryDBI.update(userId, categoryId, categoryUpdateRequestData)

        return get(userId, categoryId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        categoryId: IxId<CategoryData>,
    ): Boolean {
        val deleted = categoryDBI.delete(userId, categoryId)

        return deleted
    }
}
