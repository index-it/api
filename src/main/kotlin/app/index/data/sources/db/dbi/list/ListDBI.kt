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

    suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): Boolean

    suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) : Boolean
}
