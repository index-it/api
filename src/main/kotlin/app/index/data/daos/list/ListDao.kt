package app.index.data.daos.list

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
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
        userId: IxId<UserData>,
        listCreateRequestData: ListData.ListCreateRequestData,
    ): ListData {
        val listData = ListData(
            id = newIxId(),
            userId = userId,
            name = listCreateRequestData.name,
            icon = listCreateRequestData.icon,
            color = listCreateRequestData.color,
            createdAt = DatetimeUtils.currentMillis(),
            editedAt = null,
        )

        listDBI.create(listData)
        listCM.cache(listData.userId, listData)

        return listData
    }

    suspend fun getAll(userId: IxId<UserData>): List<ListData> {
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
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): ListData? {
        var list = listCM.get(userId, listId)

        if (list == null) {
            list = listDBI.get(userId, listId)
                ?: return null
            listCM.cache(userId, list)
        }

        return list
    }

    suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): ListData? {
        val updated = listDBI.update(userId, listId, listUpdateRequestData)

        if (updated) {
            listCM.delete(userId, listId)
        }

        return get(userId, listId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) : Boolean {
        val deleted = listDBI.delete(userId, listId)
        listCM.delete(userId, listId)

        // Update cache as it doesn't have cascade events like sql does
        categoryCMImpl.deleteAllOfList(userId, listId)

        val itemsOfDeletedList = itemCM.getAll(userId, listId)
        itemContentCM.deleteMultiple(userId, itemsOfDeletedList.map { it.id })

        itemCM.deleteAllOfList(userId, listId)

        return deleted
    }
}
