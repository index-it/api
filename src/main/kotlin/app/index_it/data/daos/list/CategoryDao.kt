package app.index_it.data.daos.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.CategoryCM
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.db.dbi.list.impl.CategoryDBIImpl

object CategoryDao {
    suspend fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<CategoryDto> {
        // TODO: Query db instead?
        var categories = CategoryCM.getAll(userId, listId)

        if (categories.isEmpty()) {
            categories = CategoryDBIImpl.getOfList(userId, listId)

            if (categories.isNotEmpty())
                CategoryCM.cacheAll(userId, listId, categories)
        }

        return categories
    }

    suspend fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): CategoryDto? {
        var category = CategoryCM.get(userId, listId, categoryId)

        if (category == null) {
            category = CategoryDBIImpl.get(userId, categoryId)
                ?: return null
            CategoryCM.cache(userId, listId, category)
        }

        return category
    }

    suspend fun create(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryCreateRequestDto: CategoryDto.CategoryCreateRequestDto): CategoryDto {
        val categoryDto = CategoryDto(
            id = newIxId(),
            userId = userId,
            listId = listId,
            name = categoryCreateRequestDto.name,
            color = categoryCreateRequestDto.color
        )

        CategoryDBIImpl.create(categoryDto)
        CategoryCM.cache(userId, listId, categoryDto)

        return categoryDto
    }

    suspend fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): CategoryDto? {
        val updated = CategoryDBIImpl.update(userId, categoryId, categoryUpdateRequestDto)

        if (updated) {
            CategoryCM.delete(userId, listId, categoryId)
        }

        return get(userId, listId, categoryId)
    }

    suspend fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>) {
        CategoryDBIImpl.delete(userId, categoryId)
        CategoryCM.delete(userId, listId, categoryId)

        val itemIdsOfCategory = ItemCM.getAll(userId, listId)
            .filter { it.categoryId == categoryId }
            .map { item -> item.id }
        ItemContentCM.deleteMultiple(userId, itemIdsOfCategory)
        ItemCM.deleteMultiple(userId, listId, itemIdsOfCategory)
    }

    /*
    fun deleteAllOfUser(userId: IxId<UserDto>) {
        CategoryDBIImpl.deleteAllOfUser(userId)
        CategoryCM.deleteAllOfUser(userId)
    }

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        CategoryDBM.deleteAllOfList(userId, listId)
        CategoryCM.deleteAllOfList(userId, listId)
    }
     */
}
