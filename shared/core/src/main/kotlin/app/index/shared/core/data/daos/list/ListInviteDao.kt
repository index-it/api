package app.index.shared.core.data.daos.list

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.ListData
import app.index.shared.core.data.models.lists.ListInviteData
import app.index.shared.core.data.sources.db.dbi.list.ListInviteDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListInviteDao(
    private val listInviteDBI: ListInviteDBI
) {
    suspend fun create(listInviteData: ListInviteData) = listInviteDBI.create(listInviteData)

    suspend fun decreaseUsages(id: IxId<ListInviteData>) = listInviteDBI.decreaseUsages(id)

    suspend fun get(token: String): ListInviteData? = listInviteDBI.get(token)

    suspend fun get(listId: IxId<ListData>): List<ListInviteData> = listInviteDBI.get(listId)

    suspend fun delete(id: IxId<ListInviteData>): Boolean = listInviteDBI.delete(id)

    suspend fun deleteExpired() = listInviteDBI.deleteExpired()
}