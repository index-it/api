package app.index.api.data.daos.auth

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.PasswordResetData
import app.index.shared.core.data.models.user.UserData
import app.index.api.data.sources.db.dbi.user.PasswordResetDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class PasswordResetDao(
    private val passwordResetDBI: PasswordResetDBI
) {
    suspend fun create(passwordResetData: PasswordResetData) = passwordResetDBI.create(passwordResetData)

    suspend fun get(token: String): PasswordResetData? = passwordResetDBI.get(token)

    /**
     * Rate limited if the user has received at least 7 password reset emails and didn't use any
     */
    suspend fun isUserRateLimited(id: IxId<UserData>): Boolean {
        return passwordResetDBI.count(id) >= 7
    }

    suspend fun deleteExpired() = passwordResetDBI.deleteExpired()
}
