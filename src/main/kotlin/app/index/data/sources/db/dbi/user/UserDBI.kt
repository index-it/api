package app.index.data.sources.db.dbi.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface UserDBI : DBI {
    suspend fun create(userData: UserData)

    suspend fun get(id: IxId<UserData>): UserData?

    suspend fun get(email: String): UserData?

    suspend fun verifyEmail(id: IxId<UserData>)

    suspend fun resetPassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    )

    suspend fun delete(id: IxId<UserData>)
}
