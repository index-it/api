package app.index.api.data.sources.db.dbi.user

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.db.dbi.DBI

interface UserDBI : DBI {
    suspend fun create(userData: UserData)

    suspend fun get(id: IxId<UserData>): UserData?

    suspend fun get(email: String): UserData?

    suspend fun verifyEmail(id: IxId<UserData>)

    suspend fun changePassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
    )

    suspend fun resetPassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    )

    suspend fun setHasPro(id: IxId<UserData>, hasPro: Boolean): UserData?

    suspend fun delete(id: IxId<UserData>)
}
