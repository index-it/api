package app.index.data.sources.db.dbi.list

import app.index.data.models.lists.ListInvitationData
import app.index.data.sources.db.dbi.DBI

interface ListInvitationDBI : DBI {
    suspend fun create(listInvitationData: ListInvitationData)

    suspend fun get(token: String): ListInvitationData?

    suspend fun deleteExpired()
}
