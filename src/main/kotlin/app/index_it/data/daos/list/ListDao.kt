package app.index_it.data.daos.list

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.CategoryCM
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.cache.cm.lists.ListCM
import app.index_it.data.sources.db.dbi.list.impl.ListDBIImpl

object ListDao {
    suspend fun create(userId: IxId<UserDto>, listCreateRequestDto: ListDto.ListCreateRequestDto): ListDto {
        val listDto = ListDto(
            id = newIxId(),
            userId = userId,
            name = listCreateRequestDto.name,
            icon = listCreateRequestDto.icon,
            color = listCreateRequestDto.color,
            createdAt = currentMillis(),
            editedAt = null
        )
        ListDBIImpl.create(listDto)
        ListCM.cache(listDto.userId, listDto)

        return listDto
    }

    suspend fun getAll(userId: IxId<UserDto>): List<ListDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var lists = ListCM.getAll(userId)

        if (lists.isEmpty()) {
            lists = ListDBIImpl.get(userId)
            if (lists.isNotEmpty())
                ListCM.cacheAll(userId, lists)
        }

        return lists
    }

    suspend fun get(userId: IxId<UserDto>, listId: IxId<ListDto>): ListDto? {
        var list = ListCM.get(userId, listId)

        if (list == null) {
            list = ListDBIImpl.get(userId, listId)
                ?: return null
            ListCM.cache(userId, list)
        }

        return list
    }

    suspend fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, listUpdateRequestDto: ListDto.ListUpdateRequestDto): ListDto? {
        val updated = ListDBIImpl.update(userId, listId, listUpdateRequestDto)

        return if (updated) {
            ListCM.delete(userId, listId)
            get(userId, listId)
        } else {
            null
        }
    }

    suspend fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        ListDBIImpl.delete(userId, listId)
        ListCM.delete(userId, listId)

        // Update cache as it doesn't have cascade events like sql does
        CategoryCM.deleteAllOfList(userId, listId)

        val itemsOfDeletedList = ItemCM.getAll(userId, listId)
        ItemContentCM.deleteMultiple(userId, itemsOfDeletedList.map { it.id })

        ItemCM.deleteAllOfList(userId, listId)
    }

    /*
    fun deleteAll(userId: IxId<UserDto>) {
        ListDBIImpl.deleteAll(userId)
        ListCM.deleteAll(userId)
    }
     */
}
