package app.index_it.daos

import app.index_it.core.clients.SendinblueClient
import app.index_it.core.db.EmailVerificationDBM
import app.index_it.models.email.EmailVerificationDto
import io.ktor.util.date.*
import java.util.*

object EmailVerificationDao {
    fun get(code: String): EmailVerificationDto? = EmailVerificationDBM.get(code)

    /**
     * Sends a verification email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(email: String): Boolean {
        val emailVerificationDto = EmailVerificationDto(
            user_email = email,
            expire_at = Date(getTimeMillis() + 3600000)
        )
        save(emailVerificationDto)
        return SendinblueClient.sendEmailVerificationEmail(email, emailVerificationDto.code)
    }

    fun save(emailVerificationDto: EmailVerificationDto) = EmailVerificationDBM.save(emailVerificationDto)

    fun delete(code: String) = EmailVerificationDBM.delete(code)

    fun isRateLimited(email: String): Boolean {
        val sent = EmailVerificationDBM.countSaved(email)
        return sent >= 3
    }
}
