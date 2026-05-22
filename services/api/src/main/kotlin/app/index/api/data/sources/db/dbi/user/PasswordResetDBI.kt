package app.index.api.data.sources.db.dbi.user

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.PasswordResetData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.db.dbi.DBI

interface PasswordResetDBI : DBI {
    suspend fun count(id: IxId<UserData>): Long

    suspend fun create(passwordResetData: PasswordResetData)

    suspend fun get(token: String): PasswordResetData?

    suspend fun deleteAll(id: IxId<UserData>)

    suspend fun deleteExpired()
}
