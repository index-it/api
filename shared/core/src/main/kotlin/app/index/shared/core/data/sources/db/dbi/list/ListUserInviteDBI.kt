package app.index.shared.core.data.sources.db.dbi.list

import app.index.shared.core.data.models.lists.ListUserInviteData
import app.index.shared.core.data.sources.db.dbi.DBI

interface ListUserInviteDBI : app.index.shared.core.data.sources.db.dbi.DBI {
    suspend fun create(listUserInviteData: ListUserInviteData)

    suspend fun get(token: String): ListUserInviteData?

    suspend fun deleteExpired()
}
