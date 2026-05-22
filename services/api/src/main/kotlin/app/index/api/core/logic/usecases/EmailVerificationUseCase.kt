package app.index.api.core.logic.usecases

import app.index.shared.core.clients.BrevoClient
import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.logic.TokenGenerator
import app.index.shared.core.data.daos.auth.EmailVerificationDao
import app.index.shared.core.data.models.email.EmailVerificationData
import app.index.shared.core.data.models.user.UserData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object EmailVerificationUseCase : KoinComponent {
    private val emailVerificationDao by inject<EmailVerificationDao>()
    private val tokenGenerator by inject<TokenGenerator>()
    private val brevoClient by inject<BrevoClient>()

    /**
     * Sends a verification email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     *
     * @return true if the email was sent, false otherwise
     */
    suspend fun createAndSend(user: UserData): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val emailVerificationData = EmailVerificationData(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000,
            createdAt = DatetimeUtils.currentMillis(),
        )

        val sent = brevoClient.sendEmailVerificationEmail(user.email, token)

        if (sent) {
            emailVerificationDao.create(emailVerificationData)
        }

        return sent
    }
}