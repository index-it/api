package app.index.data.sources.db.dbi.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.PasswordResetData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface PasswordResetDBI : DBI {
    suspend fun count(id: IxId<UserData>): Long

    suspend fun create(passwordResetData: PasswordResetData)

    suspend fun get(token: String): PasswordResetData?

    suspend fun deleteAll(id: IxId<UserData>)

    suspend fun deleteExpired()
}
