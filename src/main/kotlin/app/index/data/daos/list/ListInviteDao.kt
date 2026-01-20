package app.index.data.daos.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.lists.ListInviteData
import app.index.data.sources.db.dbi.list.ListInviteDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListInviteDao(
    private val listInviteDBI: ListInviteDBI
) {
    suspend fun create(listInviteData: ListInviteData) = listInviteDBI.create(listInviteData)

    suspend fun get(token: String): ListInviteData? = listInviteDBI.get(token)

    suspend fun get(listId: IxId<ListData>): List<ListInviteData> = listInviteDBI.get(listId)

    suspend fun delete(inviteId: IxId<ListInviteData>): Boolean = listInviteDBI.delete(inviteId)

    suspend fun deleteExpired() = listInviteDBI.deleteExpired()
}