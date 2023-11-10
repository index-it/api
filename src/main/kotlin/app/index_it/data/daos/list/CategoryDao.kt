package app.index_it.data.daos.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.lists.toDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.CategoryCM
import app.index_it.data.sources.db.schemas.lists.CategoryEntity
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.mongo.lists.CategoryDBM
import app.index_it.data.sources.db.toEntityId

object CategoryDao {
    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<CategoryDto> {
        // TODO: Query db instead?
        var categories = CategoryCM.getAll(userId, listId)

        if (categories.isEmpty()) {
            categories = CategoryEntity
                .find { CategoryTable.list eq listId.toEntityId(ListTable) }
                .map { it.toDto() }

            if (categories.isNotEmpty())
                CategoryCM.cacheAll(userId, listId, categories)
        }

        return categories
    }

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): CategoryDto? {
        var category = CategoryCM.get(userId, listId, categoryId)

        if (category == null) {
            category = CategoryDBM.get(userId, listId, categoryId)
                ?: return null
            CategoryCM.cache(userId, listId, category)
        }

        return category
    }

    fun create(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryCreateRequestDto: CategoryDto.CategoryCreateRequestDto): CategoryDto {
        val categoryDto = CategoryDto(
            userId = userId,
            listId = listId,
            name = categoryCreateRequestDto.name,
            color = categoryCreateRequestDto.color
        )

        CategoryDBM.create(categoryDto)
        CategoryCM.cache(userId, listId, categoryDto)

        return categoryDto
    }

    fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): CategoryDto? {
        val category = CategoryDBM.update(userId, listId, categoryId, categoryUpdateRequestDto)

        if (category != null)
            CategoryCM.cache(userId, listId, category)
        else
            CategoryCM.delete(userId, listId, categoryId)

        return category
    }

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>) {
        CategoryDBM.delete(userId, listId, categoryId)
        CategoryCM.delete(userId, listId, categoryId)
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        CategoryDBM.deleteAllOfUser(userId)
        CategoryCM.deleteAllOfUser(userId)
    }

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        CategoryDBM.deleteAllOfList(userId, listId)
        CategoryCM.deleteAllOfList(userId, listId)
    }
}
