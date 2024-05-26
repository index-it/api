package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface ListDBI : DBI {
    suspend fun create(listData: ListData)

    suspend fun get(id: IxId<UserData>): List<ListData>

    suspend fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): ListData?


    suspend fun getByIdOnly(
        listId: IxId<ListData>,
    ): ListData?

    suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): Boolean

    /**
     * Gives user permission to either view or edit a list
     * This already handles mutual exclusiveness between viewers and editors
     */
    suspend fun addPermissionToUser(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        userToAddId: IxId<UserData>,
        editor: Boolean
    ): Boolean

    /**
     * Removes a user access to a list completely (both viewing and editing)
     */
    suspend fun removePermissionFromUser(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        userToRemoveId: IxId<UserData>
    ): Boolean

    suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) : Boolean
}
