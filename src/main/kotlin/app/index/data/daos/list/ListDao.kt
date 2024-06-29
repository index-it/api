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
        return listDBI.getAllOfUser(userId)
    }

    /**
     * Gets all lists that the user has access to (meaning he is either a viewer, editor or owner)
     */
    suspend fun getListsAccessibleByUser(userId: IxId<UserData>): List<ListData> {
        return listDBI.getListsAccessibleByUser(userId)
    }

    /**
     * Gets a list by its [listId]
     */
    suspend fun get(listId: IxId<ListData>): ListData? {
        return listDBI.get(listId)
    }

    /**
     * Gets infos about the users that have access to a list, null if the list does not exist
     */
    suspend fun getListUserAccessInfo(listId: IxId<ListData>): List<ListData.ListSingleUserAccessInfoResponseData>? {
        return listDBI.getListUserAccessInfo(listId)
    }

    suspend fun update(
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): ListData? {
        return listDBI.update(listId, listUpdateRequestData)
    }

    /**
     * Gives user permission to either view or edit a list
     * This already handles mutual exclusiveness between viewers and editors
     */
    suspend fun addPermissionToUser(
        listId: IxId<ListData>,
        userToAddId: IxId<UserData>,
        editor: Boolean
    ): ListData? {
        listDBI.addPermissionToUser(listId, userToAddId, editor)

        return get(listId)
    }

    /**
     * Removes a user access to a list completely (both viewing and editing)
     */
    suspend fun removePermissionFromUser(
        listId: IxId<ListData>,
        userToRemoveId: IxId<UserData>,
    ): ListData? {
        listDBI.removePermissionFromUser(listId, userToRemoveId)

        return get(listId)
    }

    suspend fun delete(
        listId: IxId<ListData>,
    ) : Boolean {
        val deleted = listDBI.delete(listId)

        return deleted
    }
}