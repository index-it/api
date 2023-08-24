package app.index_it.daos.list

import app.index_it.core.cache.lists.ListCM
import app.index_it.core.db.lists.ListDBM
import app.index_it.core.logic.currentMillis
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ListDao {
    fun create(userId: Id<UserDto>, listCreateRequestDto: ListDto.ListCreateRequestDto): ListDto {
        val listDto = ListDto(
            userId = userId,
            name = listCreateRequestDto.name,
            icon = listCreateRequestDto.icon,
            color = listCreateRequestDto.color,
            createdAt = currentMillis(),
            editedAt = null
        )
        ListDBM.create(listDto)
        ListCM.cache(listDto.userId, listDto)

        return listDto
    }

    fun getAll(userId: Id<UserDto>): List<ListDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var lists = ListCM.getAll(userId)

        if (lists.isEmpty()) {
            lists = ListDBM.getAll(userId)
            if (lists.isNotEmpty())
                ListCM.cacheAll(userId, lists)
        }

        return lists
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>): ListDto? {
        var list = ListCM.get(userId, listId)

        if (list == null) {
            list = ListDBM.get(listId)
                ?: return null
            ListCM.cache(userId, list)
        }

        return list
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, listUpdateRequestDto: ListDto.ListUpdateRequestDto): ListDto? {
        val listDto = ListDBM.update(userId, listId, listUpdateRequestDto)

        if (listDto != null)
            ListCM.update(userId, listDto)
        else
            ListCM.delete(userId, listId)

        return listDto
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>) {
        ListDBM.delete(userId, listId)
        ListCM.delete(userId, listId)
    }

    fun deleteAll(userId: Id<UserDto>) {
        ListDBM.deleteAll(userId)
        ListCM.deleteAll(userId)
    }
}
