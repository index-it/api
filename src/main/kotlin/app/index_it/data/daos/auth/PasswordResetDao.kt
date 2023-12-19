package app.index_it.data.daos.auth

import app.index_it.core.clients.BrevoClient
import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.PasswordResetDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.impl.PasswordResetDBIImpl

object PasswordResetDao {
    private suspend fun save(passwordResetDto: PasswordResetDto) =
        PasswordResetDBIImpl.create(passwordResetDto)

    suspend fun get(token: String): PasswordResetDto? =
        PasswordResetDBIImpl.get(token)

    /**
     * Sends a password reset email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserDto): Boolean {
        val (token, hashedToken) = TokenGenerator.generate()

        val passwordResetDto = PasswordResetDto(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000
        )

        save(passwordResetDto)
        return BrevoClient.sendPasswordResetEmail(user.email, token)
    }

    /**
     * Up to 7 password resets in an hour
     */
    suspend fun isRateLimited(id: IxId<UserDto>): Boolean {
        val sent = PasswordResetDBIImpl.count(id)
        return sent >= 7
    }
}
