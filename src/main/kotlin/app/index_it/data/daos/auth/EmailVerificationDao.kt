package app.index_it.data.daos.auth

import app.index_it.core.clients.SendinblueClient
import app.index_it.core.logic.TokenGenerator
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.users.EmailVerificationDBM
import io.ktor.util.date.*
import org.litote.kmongo.Id
import java.util.*

object EmailVerificationDao {
    private fun save(emailVerificationDto: EmailVerificationDto) = EmailVerificationDBM.save(emailVerificationDto)

    fun get(token: String): EmailVerificationDto? {
        return EmailVerificationDBM.get(TokenGenerator.hashToken(token))
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
            expireAt = Date(getTimeMillis() + 3600000)
        )

        app.index_it.data.daos.auth.EmailVerificationDao.save(emailVerificationDto)
        return SendinblueClient.sendEmailVerificationEmail(user.email, token)
    }

    /**
     * Deletes all email verification tokens of a specific email.
     */
    fun deleteAll(id: Id<UserDto>) = EmailVerificationDBM.deleteAll(id)

    fun isRateLimited(id: Id<UserDto>): Boolean {
        val sent = EmailVerificationDBM.countSaved(id)
        return sent >= 5
    }
}
