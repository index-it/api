package app.index_it.daos.list

import app.index_it.core.cache.ListCM
import app.index_it.core.db.CategoryDBM
import app.index_it.core.db.ListDBM
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object CategoryDao {
    fun create(userId: Id<UserDto>, listId: Id<ListDto>, categoryCreateRequestDto: CategoryDto.CategoryCreateRequestDto): ListDto? {
        val categoryDto = CategoryDto(
            name = categoryCreateRequestDto.name,
            color = categoryCreateRequestDto.color
        )

        val listDto = CategoryDBM.create(userId, listId, categoryDto)
        if (listDto != null)
            ListCM.update(userId, listDto)
        else
            ListCM.delete(userId, listId)

        return listDto
    }

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<CategoryDto>? {
        var categories = ListCM.get(userId, listId)?.categories

        if (categories == null) {
            categories = CategoryDBM.getAll(userId, listId)
            ListCM.delete(userId, listId)
        }

        return categories
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): CategoryDto? {
        var category = ListCM.get(userId, listId)?.categories?.firstOrNull { it.id == categoryId }

        if (category == null) {
            category = CategoryDBM.get(userId, listId, categoryId)
            ListCM.delete(userId, listId)
        }

        return category
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): ListDto? {
        val listDto = CategoryDBM.update(userId, listId, categoryId, categoryUpdateRequestDto)
        if (listDto != null)
            ListCM.update(userId, listDto)
        else
            ListCM.delete(userId, listId)

        return listDto
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): ListDto? {
        val listDto = CategoryDBM.delete(userId, listId, categoryId)
        if (listDto != null)
            ListCM.update(userId, listDto)
        else
            ListCM.delete(userId, listId)

        return listDto
    }
}
