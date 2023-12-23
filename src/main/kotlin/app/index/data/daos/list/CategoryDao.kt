package app.index.data.daos.list

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.lists.CategoryCM
import app.index.data.sources.cache.cm.lists.ItemCM
import app.index.data.sources.cache.cm.lists.ItemContentCM
import app.index.data.sources.db.dbi.list.CategoryDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CategoryDao(
    private val categoryDBI: CategoryDBI,
    private val categoryCM: CategoryCM,
    private val itemCM: ItemCM,
    private val itemContentCM: ItemContentCM,
) {
    suspend fun getAll(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<CategoryData> {
        var categories = categoryCM.getAll(userId, listId)

        if (categories.isEmpty()) {
            categories = categoryDBI.getOfList(userId, listId)

            if (categories.isNotEmpty()) {
                categoryCM.cacheAll(userId, listId, categories)
            }
        }

        return categories
    }

    suspend fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
    ): CategoryData? {
        var category = categoryCM.get(userId, listId, categoryId)

        if (category == null) {
            category = categoryDBI.get(userId, categoryId)
                ?: return null
            categoryCM.cache(userId, listId, category)
        }

        return category
    }

    suspend fun create(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryCreateRequestData: CategoryData.CategoryCreateRequestData,
    ): CategoryData {
        val categoryData = CategoryData(
            id = newIxId(),
            userId = userId,
            listId = listId,
            name = categoryCreateRequestData.name,
            color = categoryCreateRequestData.color,
        )

        categoryDBI.create(categoryData)
        categoryCM.cache(userId, listId, categoryData)

        return categoryData
    }

    suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
        categoryUpdateRequestData: CategoryData.CategoryUpdateRequestData,
    ): CategoryData? {
        val updated = categoryDBI.update(userId, categoryId, categoryUpdateRequestData)

        if (updated) {
            categoryCM.delete(userId, listId, categoryId)
        }

        return get(userId, listId, categoryId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        categoryId: IxId<CategoryData>,
    ): Boolean {
        val deleted = categoryDBI.delete(userId, categoryId)

        categoryCM.delete(userId, listId, categoryId)

        val itemIdsOfCategory = itemCM.getAll(userId, listId)
            .filter { it.categoryId == categoryId }
            .map { item -> item.id }
        itemContentCM.deleteMultiple(userId, itemIdsOfCategory)
        itemCM.deleteMultiple(userId, listId, itemIdsOfCategory)

        return deleted
    }
}
