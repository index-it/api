package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("brevo")
object BrevoConfig {
    @ConfigurationProperty("api.key")
    var apiKey: String = "none"

    @ConfigurationProperty("template.email.verification")
    var emailVerificationTemplateId: Long = 2

    @ConfigurationProperty("template.password.reset")
    var passwordResetTemplateId: Long = 1

    @ConfigurationProperty("template.password.reset.success")
    var passwordResetSuccessTemplateId: Long = 3

    @ConfigurationProperty("template.list.invite")
    var listInvitationTemplateId: Long = 5

    @ConfigurationProperty("email.verification.success.url")
    var emailVerificationSuccessUrl: String = "https://index-it.app/email-verified"

    @ConfigurationProperty("email.verification.error.url")
    var emailVerificationErrorUrl: String = "https://index-it.app/invalid-email-verification-code"

    @ConfigurationProperty("email.verification.url")
    var emailVerificationUrl: String = "http://localhost:8080/verify-email"

    @ConfigurationProperty("reset.password.url")
    var passwordResetUrl: String = "https://index-it.app/reset-password"

    @ConfigurationProperty("list.user.invite.url")
    var listUserInviteUrl: String = "https://index-it.app/callback/lists/accept-user-invitation"
}
