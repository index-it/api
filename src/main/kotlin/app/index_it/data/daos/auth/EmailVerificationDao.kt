package app.index_it.data.daos.auth

import app.index_it.core.clients.SendinblueClient
import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.impl.EmailVerificationDBIImpl

object EmailVerificationDao {
    private suspend fun save(emailVerificationDto: EmailVerificationDto) =
        EmailVerificationDBIImpl.save(emailVerificationDto)


    suspend fun get(token: String): EmailVerificationDto? {
        return EmailVerificationDBIImpl.get(token)
    }

    /**
     * Sends a verification email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserDto): Boolean {
        val (token, hashedToken) = TokenGenerator.generate()

        val emailVerificationDto = EmailVerificationDto(
            token = hashedToken,
            userId = user.id,
            expireAt = currentMillis() + 3600000,
            createdAt = currentMillis()
        )

        save(emailVerificationDto)
        return SendinblueClient.sendEmailVerificationEmail(user.email, token)
    }

    /**
     * Deletes all email verification tokens of a specific email.
     */
    suspend fun deleteAll(id: IxId<UserDto>) = EmailVerificationDBIImpl.deleteAll(id)

    suspend fun isRateLimited(id: IxId<UserDto>): Boolean {
        val sent = EmailVerificationDBIImpl.count(id)
        return sent >= 5
    }
}
