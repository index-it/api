package app.index.data.daos.list

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto
import app.index.data.sources.cache.cm.lists.ItemCM
import app.index.data.sources.cache.cm.lists.ItemContentCM
import app.index.data.sources.cache.cm.lists.ListCM
import app.index.data.sources.cache.cm.lists.impl.CategoryCMImpl
import app.index.data.sources.db.dbi.list.ListDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListDao(
    private val listDBI: ListDBI,
    private val listCM: ListCM,
    private val categoryCMImpl: CategoryCMImpl,
    private val itemCM: ItemCM,
    private val itemContentCM: ItemContentCM,
) {
    suspend fun create(
        userId: IxId<UserDto>,
        listCreateRequestDto: ListDto.ListCreateRequestDto,
    ): ListDto {
        val listDto =
            ListDto(
                id = newIxId(),
                userId = userId,
                name = listCreateRequestDto.name,
                icon = listCreateRequestDto.icon,
                color = listCreateRequestDto.color,
                createdAt = DatetimeUtils.currentMillis(),
                editedAt = null,
            )
        listDBI.create(listDto)
        listCM.cache(listDto.userId, listDto)

        return listDto
    }

    suspend fun getAll(userId: IxId<UserDto>): List<ListDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var lists = listCM.getAll(userId)

        if (lists.isEmpty()) {
            lists = listDBI.get(userId)
            if (lists.isNotEmpty()) {
                listCM.cacheAll(userId, lists)
            }
        }

        return lists
    }

    suspend fun get(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): ListDto? {
        var list = listCM.get(userId, listId)

        if (list == null) {
            list = listDBI.get(userId, listId)
                ?: return null
            listCM.cache(userId, list)
        }

        return list
    }

    suspend fun update(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        listUpdateRequestDto: ListDto.ListUpdateRequestDto,
    ): ListDto? {
        val updated = listDBI.update(userId, listId, listUpdateRequestDto)

        if (updated) {
            listCM.delete(userId, listId)
        }

        return get(userId, listId)
    }

    suspend fun delete(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ) {
        listDBI.delete(userId, listId)
        listCM.delete(userId, listId)

        // Update cache as it doesn't have cascade events like sql does
        categoryCMImpl.deleteAllOfList(userId, listId)

        val itemsOfDeletedList = itemCM.getAll(userId, listId)
        itemContentCM.deleteMultiple(userId, itemsOfDeletedList.map { it.id })

        itemCM.deleteAllOfList(userId, listId)
    }

    /*
    fun deleteAll(userId: IxId<UserDto>) {
        listDBI.deleteAll(userId)
        ListCM.deleteAll(userId)
    }
     */
}
