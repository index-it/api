package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("revenuecat")
object RevenueCatConfig {
    @ConfigurationProperty("api.key")
    lateinit var apiKey: String

    @ConfigurationProperty("webhook.secret")
    lateinit var webhookSecret: String

    @ConfigurationProperty("sandbox")
    var sandbox: Boolean = false
}