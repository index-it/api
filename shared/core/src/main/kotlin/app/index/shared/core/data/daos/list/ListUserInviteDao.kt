package app.index.shared.core.data.daos.list

import app.index.shared.core.data.models.lists.ListUserInviteData
import app.index.shared.core.data.sources.db.dbi.list.ListUserInviteDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListUserInviteDao(
    private val listUserInviteDBI: ListUserInviteDBI
) {
    suspend fun create(listUserInviteData: ListUserInviteData) = listUserInviteDBI.create(listUserInviteData)

    suspend fun get(token: String): ListUserInviteData? = listUserInviteDBI.get(token)

    suspend fun deleteExpired() = listUserInviteDBI.deleteExpired()
}
