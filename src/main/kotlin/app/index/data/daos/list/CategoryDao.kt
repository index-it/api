package app.index.data.daos.list

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.CategoryDto
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto
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
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): List<CategoryDto> {
        // TODO: Query db instead?
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
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryId: IxId<CategoryDto>,
    ): CategoryDto? {
        var category = categoryCM.get(userId, listId, categoryId)

        if (category == null) {
            category = categoryDBI.get(userId, categoryId)
                ?: return null
            categoryCM.cache(userId, listId, category)
        }

        return category
    }

    suspend fun create(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryCreateRequestDto: CategoryDto.CategoryCreateRequestDto,
    ): CategoryDto {
        val categoryDto =
            CategoryDto(
                id = newIxId(),
                userId = userId,
                listId = listId,
                name = categoryCreateRequestDto.name,
                color = categoryCreateRequestDto.color,
            )

        categoryDBI.create(categoryDto)
        categoryCM.cache(userId, listId, categoryDto)

        return categoryDto
    }

    suspend fun update(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryId: IxId<CategoryDto>,
        categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto,
    ): CategoryDto? {
        val updated = categoryDBI.update(userId, categoryId, categoryUpdateRequestDto)

        if (updated) {
            categoryCM.delete(userId, listId, categoryId)
        }

        return get(userId, listId, categoryId)
    }

    suspend fun delete(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        categoryId: IxId<CategoryDto>,
    ) {
        categoryDBI.delete(userId, categoryId)
        categoryCM.delete(userId, listId, categoryId)

        val itemIdsOfCategory =
            itemCM.getAll(userId, listId)
                .filter { it.categoryId == categoryId }
                .map { item -> item.id }
        itemContentCM.deleteMultiple(userId, itemIdsOfCategory)
        itemCM.deleteMultiple(userId, listId, itemIdsOfCategory)
    }
}
