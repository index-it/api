package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("brevo")
object BrevoConfig {
    @ConfigurationProperty("api.key")
    lateinit var apiKey: String

    @ConfigurationProperty("email.verification.success.url")
    lateinit var emailVerificationSuccessUrl: String

    @ConfigurationProperty("email.verification.error.url")
    lateinit var emailVerificationErrorUrl: String

    @ConfigurationProperty("email.verification.url")
    lateinit var emailVerificationUrl: String

    @ConfigurationProperty("reset.password.url")
    lateinit var passwordResetUrl: String
}
