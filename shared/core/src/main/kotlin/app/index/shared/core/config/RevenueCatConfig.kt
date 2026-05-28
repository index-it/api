package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("revenuecat")
object RevenueCatConfig {
    @ConfigurationProperty("api.key")
    lateinit var apiKey: String

    @ConfigurationProperty("webhook.secret")
    lateinit var webhookSecret: String

    @ConfigurationProperty("sandbox")
    var sandbox: Boolean = false
}