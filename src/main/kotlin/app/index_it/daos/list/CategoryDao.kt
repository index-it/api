package app.index_it.daos.list

import app.index_it.core.cache.CategoryCM
import app.index_it.core.db.CategoryDBM
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object CategoryDao {
    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<CategoryDto> {
        // TODO: Query db instead?
        var categories = CategoryCM.getAll(userId, listId)

        if (categories.isEmpty()) {
            categories = CategoryDBM.getAll(userId, listId)
            if (categories.isNotEmpty())
                CategoryCM.cacheAll(userId, listId, categories)
        }

        return categories
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): CategoryDto? {
        var category = CategoryCM.get(userId, listId, categoryId)

        if (category == null) {
            category = CategoryDBM.get(userId, listId, categoryId)
                ?: return null
            CategoryCM.cache(userId, listId, category)
        }

        return category
    }

    fun create(userId: Id<UserDto>, listId: Id<ListDto>, categoryCreateRequestDto: CategoryDto.CategoryCreateRequestDto): CategoryDto {
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

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): CategoryDto? {
        val category = CategoryDBM.update(userId, listId, categoryId, categoryUpdateRequestDto)

        if (category != null)
            CategoryCM.cache(userId, listId, category)
        else
            CategoryCM.delete(userId, listId, categoryId)

        return category
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>) {
        CategoryDBM.delete(userId, listId, categoryId)
        CategoryCM.delete(userId, listId, categoryId)
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        CategoryDBM.deleteAllOfUser(userId)
        CategoryCM.deleteAllOfUser(userId)
    }

    fun deleteAllOfList(userId: Id<UserDto>, listId: Id<ListDto>) {
        CategoryDBM.deleteAllOfList(userId, listId)
        CategoryCM.deleteAllOfList(userId, listId)
    }
}
