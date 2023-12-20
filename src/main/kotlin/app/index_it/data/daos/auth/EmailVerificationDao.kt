package app.index_it.data.daos.auth

import app.index_it.core.clients.BrevoClient
import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.EmailVerificationDBI
import app.index_it.data.sources.db.dbi.user.impl.EmailVerificationDBIImpl
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class EmailVerificationDao(
    private val emailVerificationDBI: EmailVerificationDBI,
    private val tokenGenerator: TokenGenerator,
    private val brevoClient: BrevoClient
) {
    private suspend fun save(emailVerificationDto: EmailVerificationDto) =
        emailVerificationDBI.create(emailVerificationDto)


    suspend fun get(token: String): EmailVerificationDto? {
        return emailVerificationDBI.get(token)
    }

    /**
     * Sends a verification email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserDto): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val emailVerificationDto = EmailVerificationDto(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000,
            createdAt = DatetimeUtils.currentMillis()
        )

        save(emailVerificationDto)
        return brevoClient.sendEmailVerificationEmail(user.email, token)
    }

    /**
     * Deletes all email verification tokens of a specific email.
     */
    suspend fun deleteAll(id: IxId<UserDto>) = emailVerificationDBI.deleteAll(id)

    suspend fun isRateLimited(id: IxId<UserDto>): Boolean {
        val sent = emailVerificationDBI.count(id)
        return sent >= 5
    }
}
