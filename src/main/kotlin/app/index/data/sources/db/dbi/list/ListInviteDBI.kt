package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.lists.ListInviteData
import app.index.data.sources.db.dbi.DBI

interface ListInviteDBI : DBI {
    suspend fun create(listInviteData: ListInviteData)

    suspend fun get(token: String): ListInviteData?

    suspend fun get(listId: IxId<ListData>): List<ListInviteData>

    suspend fun delete(inviteId: IxId<ListInviteData>): Boolean

    suspend fun deleteExpired()
}
