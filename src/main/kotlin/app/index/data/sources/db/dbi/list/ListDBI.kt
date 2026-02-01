package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface ListDBI : DBI {
    suspend fun create(listData: ListData)

    suspend fun getAllOfUser(id: IxId<UserData>): List<ListData>

    /**
     * Gets all lists that the user has access to
     */
    suspend fun getListsAccessibleByUser(id: IxId<UserData>): List<ListData>

    /**
     * Gets a single list by its [listId]
     */
    suspend fun get(listId: IxId<ListData>): ListData?

    suspend fun get(listIds: List<IxId<ListData>>): List<ListData>

    /**
     * Gets infos about the users that have access to a list, null if the list does not exist
     */
    suspend fun getListUserAccessInfo(listId: IxId<ListData>): List<ListData.ListSingleUserAccessInfoResponseData>?

    /**
     * Counts the number of lists the user with the specified [id] has
     */
    suspend fun count(id: IxId<UserData>): Long

    suspend fun update(
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData
    ): ListData?

    /**
     * Gives user permission to either view or edit a list
     * This already handles mutual exclusiveness between viewers and editors
     */
    suspend fun addPermissionToUser(
        listId: IxId<ListData>,
        userToAddId: IxId<UserData>,
        editor: Boolean
    )

    /**
     * Removes a user access to a list completely (both viewing and editing)
     */
    suspend fun removePermissionFromUser(
        listId: IxId<ListData>,
        userToRemoveId: IxId<UserData>
    )

    suspend fun delete(listId: IxId<ListData>) : Boolean
}
