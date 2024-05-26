package app.index.data.daos.list

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.ListDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListDao(
    private val listDBI: ListDBI,
) {
    suspend fun create(
        userId: IxId<UserData>,
        listCreateRequestData: ListData.ListCreateRequestData,
    ): ListData {
        val listData = ListData(
            id = newIxId(),
            user_id = userId,
            name = listCreateRequestData.name,
            icon = listCreateRequestData.icon,
            color = listCreateRequestData.color,
            public = listCreateRequestData.public,
            viewers = emptyList(),
            editors = emptyList(),
            created_at = DatetimeUtils.currentMillis(),
            edited_at = null,
        )

        listDBI.create(listData)

        return listData
    }

    suspend fun getAll(userId: IxId<UserData>): List<ListData> {
        return listDBI.get(userId)
    }

    suspend fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): ListData? {
        return listDBI.get(userId, listId)
    }

    suspend fun getByIdOnly(
        listId: IxId<ListData>,
    ): ListData? {
        return listDBI.getByIdOnly(listId)
    }

    suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): ListData? {
        listDBI.update(userId, listId, listUpdateRequestData)

        return get(userId, listId)
    }

    /**
     * Gives user permission to either view or edit a list
     * This already handles mutual exclusiveness between viewers and editors
     */
    suspend fun addPermissionToUser(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        userToAddId: IxId<UserData>,
        editor: Boolean
    ): ListData? {
        listDBI.addPermissionToUser(userId, listId, userToAddId, editor)

        return get(userId, listId)
    }

    /**
     * Removes a user access to a list completely (both viewing and editing)
     */
    suspend fun removePermissionFromUser(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        userToRemoveId: IxId<UserData>,
    ): ListData? {
        listDBI.removePermissionFromUser(userId, listId, userToRemoveId)

        return get(userId, listId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) : Boolean {
        val deleted = listDBI.delete(userId, listId)

        return deleted
    }
}
