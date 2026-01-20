package app.index.data.sources.db.dbi.list

import app.index.data.models.lists.ListUserInviteData
import app.index.data.sources.db.dbi.DBI

interface ListUserInviteDBI : DBI {
    suspend fun create(listUserInviteData: ListUserInviteData)

    suspend fun get(token: String): ListUserInviteData?

    suspend fun deleteExpired()
}
