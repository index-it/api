package app.index.api.data.sources.db.dbi.list

import app.index.shared.core.data.models.lists.ListUserInviteData
import app.index.api.data.sources.db.dbi.DBI

interface ListUserInviteDBI : DBI {
    suspend fun create(listUserInviteData: ListUserInviteData)

    suspend fun get(token: String): ListUserInviteData?

    suspend fun deleteExpired()
}
