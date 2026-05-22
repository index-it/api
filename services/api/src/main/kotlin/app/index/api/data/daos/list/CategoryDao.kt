package app.index.api.data.daos.list

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.core.logic.typedId.newIxId
import app.index.api.data.models.lists.CategoryData
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.db.dbi.list.CategoryDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CategoryDao(
    private val categoryDBI: CategoryDBI,
) {
    suspend fun getAll(listId: IxId<ListData>): List<CategoryData> {
        return categoryDBI.getOfList(listId)
    }

    suspend fun get(categoryId: IxId<CategoryData>): CategoryData? {
        return categoryDBI.get(categoryId)
    }

    suspend fun get(categoryIds: List<IxId<CategoryData>>): List<CategoryData> {
        return categoryDBI.get(categoryIds)
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
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): CategoryData? {
        return categoryDBI.update(categoryId, categoryUpdateRequestData)
    }

    suspend fun delete(categoryId: IxId<CategoryData>): Boolean {
        val deleted = categoryDBI.delete(categoryId)

        return deleted
    }
}
