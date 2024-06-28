package app.index.data.daos.list

import app.index.data.models.lists.ListInvitationData
import app.index.data.sources.db.dbi.list.ListInvitationDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListInvitationDao(
    private val listInvitationDBI: ListInvitationDBI
) {
    suspend fun create(listInvitationData: ListInvitationData) = listInvitationDBI.create(listInvitationData)

    suspend fun get(token: String): ListInvitationData? = listInvitationDBI.get(token)

    suspend fun deleteExpired() = listInvitationDBI.deleteExpired()
}
