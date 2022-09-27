package app.index_it.daos

import app.index_it.core.cache.ListCM
import app.index_it.core.db.ListDBM
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ClientCategoryDto
import app.index_it.models.lists.ClientListDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ListDao {

    object CategoryDao {
        fun create(userId: Id<UserDto>, listId: Id<ListDto>, clientCategoryDto: ClientCategoryDto): ListDto? {
            val categoryDto = CategoryDto(
                name = clientCategoryDto.name,
                color = clientCategoryDto.color
            )

            val listDto = ListDBM.CategoryDBM.create(userId, listId, categoryDto)
            if (listDto != null)
                ListCM.update(userId, listDto)
            else
                ListCM.delete(userId, listId)

            return listDto
        }

        fun update(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>, clientCategoryDto: ClientCategoryDto): ListDto? {
            val listDto = ListDBM.CategoryDBM.update(userId, listId, categoryId, clientCategoryDto)
            if (listDto != null)
                ListCM.update(userId, listDto)
            else
                ListCM.delete(userId, listId)

            return listDto
        }

        fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): ListDto? {
            val listDto = ListDBM.CategoryDBM.delete(userId, listId, categoryId)
            if (listDto != null)
                ListCM.update(userId, listDto)
            else
                ListCM.delete(userId, listId)

            return listDto
        }
    }

    fun create(userId: Id<UserDto>, clientListDto: ClientListDto) {
        val listDto = ListDto(
            user_id = userId,
            name = clientListDto.name,
            icon = clientListDto.icon,
            color = clientListDto.color
        )
        ListDBM.create(listDto)
        ListCM.create(listDto.user_id, listDto)
    }

    fun getAll(userId: Id<UserDto>): List<ListDto> {
        var lists = ListCM.getAll(userId)

        if (lists.isEmpty()) {
            lists = ListDBM.getAll(userId)
            if (lists.isNotEmpty())
                ListCM.createAll(userId, lists)
        }

        return lists
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, clientListDto: ClientListDto): ListDto? {
        return ListDBM.update(userId, listId, clientListDto)?.let {
            ListCM.update(userId, it)
            it
        } ?: run {
            ListCM.delete(userId, listId)
            null
        }
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>) {
        ListDBM.delete(userId, listId)
        ListCM.delete(userId, listId)
    }
}
