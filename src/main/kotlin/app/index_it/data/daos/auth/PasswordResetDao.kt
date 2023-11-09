package app.index_it.data.daos.auth

import app.index_it.core.clients.SendinblueClient
import app.index_it.data.sources.mongo.users.PasswordResetDBM
import app.index_it.core.logic.TokenGenerator
import app.index_it.data.models.user.PasswordResetDto
import app.index_it.data.models.user.UserDto
import io.ktor.util.date.*
import org.litote.kmongo.Id
import java.util.*

object PasswordResetDao {
    private fun save(passwordResetDto: PasswordResetDto) = PasswordResetDBM.save(passwordResetDto)

    fun get(token: String): PasswordResetDto? {
        return PasswordResetDBM.get(TokenGenerator.hashToken(token))
    }

    /**
     * Sends a password reset email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserDto): Boolean {
        val (token, hashedToken) = TokenGenerator.generate()

        val passwordResetDto = PasswordResetDto(
            token = hashedToken,
            userId = user.id,
            expireAt = Date(getTimeMillis() + 3600000)
        )

        app.index_it.data.daos.auth.PasswordResetDao.save(passwordResetDto)
        return SendinblueClient.sendPasswordResetEmail(user.email, token)
    }

    /**
     * Up to 7 password resets in an hour
     */
    fun isRateLimited(id: Id<UserDto>): Boolean {
        val sent = PasswordResetDBM.countSaved(id)
        return sent >= 7
    }
}
