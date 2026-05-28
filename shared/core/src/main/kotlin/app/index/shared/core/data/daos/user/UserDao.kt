package app.index.shared.core.data.daos.user

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.UserData
import app.index.shared.core.data.sources.db.dbi.user.UserDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserDao(
    private val userDBI: UserDBI,
) {
    suspend fun create(userData: UserData) {
        userDBI.create(userData)
    }

    suspend fun get(id: IxId<UserData>): UserData? {
        return userDBI.get(id)
    }

    /**
     * This method should be only used in the login route or list invitation route
     */
    suspend fun getFromEmail(email: String): UserData? {
        return userDBI.get(email)
    }

    suspend fun verifyEmail(id: IxId<UserData>) {
        userDBI.verifyEmail(id)
    }

    suspend fun changePassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
    ) {
        userDBI.changePassword(id, newPasswordHashed)
    }

    suspend fun resetPassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    ) {
        userDBI.resetPassword(id, newPasswordHashed, verifyEmail)
    }

    suspend fun setHasPro(id: IxId<UserData>, hasPro: Boolean): UserData? {
        return userDBI.setHasPro(id, hasPro)
    }

    suspend fun delete(id: IxId<UserData>) {
        userDBI.delete(id)
    }
}
