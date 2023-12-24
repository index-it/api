package app.index.core.logic.usecases

import app.index.core.clients.BrevoClient
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.TokenGenerator
import app.index.data.daos.auth.PasswordResetDao
import app.index.data.models.user.PasswordResetData
import app.index.data.models.user.UserData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PasswordResetUseCase : KoinComponent {
    private val passwordResetDao by inject<PasswordResetDao>()
    private val tokenGenerator by inject<TokenGenerator>()
    private val brevoClient by inject<BrevoClient>()

    /**
     * Sends a password reset email to the provided [user]
     *
     * @returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserData): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val passwordResetData = PasswordResetData(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000,
        )

        val sent = brevoClient.sendPasswordResetEmail(user.email, token)

        if (sent) {
            passwordResetDao.create(passwordResetData)
        }

        return sent
    }
}