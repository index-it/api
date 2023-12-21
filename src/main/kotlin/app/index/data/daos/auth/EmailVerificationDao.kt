package app.index.data.daos.auth

import app.index.core.clients.BrevoClient
import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.email.EmailVerificationData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.EmailVerificationDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class EmailVerificationDao(
    private val emailVerificationDBI: EmailVerificationDBI,
    private val tokenGenerator: TokenGenerator,
    private val brevoClient: BrevoClient,
) {
    suspend fun create(emailVerificationData: EmailVerificationData) =
        emailVerificationDBI.create(emailVerificationData)

    suspend fun get(token: String): EmailVerificationData? {
        return emailVerificationDBI.get(token)
    }

    /**
     * Deletes all email verification tokens of a specific email.
     */
    suspend fun deleteAllOfUser(id: IxId<UserData>) = emailVerificationDBI.deleteAll(id)

    /**
     * Rate limited if the user has received at least 5 verification emails and didn't use any
     */
    suspend fun isUserRateLimited(id: IxId<UserData>): Boolean {
        return emailVerificationDBI.count(id) >= 5
    }
}
