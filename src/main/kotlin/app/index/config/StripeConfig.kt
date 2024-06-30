package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("stripe")
object StripeConfig {
    @ConfigurationProperty("api.key")
    lateinit var apiKey: String

    @ConfigurationProperty("webhook.secret")
    lateinit var webhookSecret: String

    @ConfigurationProperty("enabled")
    var enabled: Boolean = true
}