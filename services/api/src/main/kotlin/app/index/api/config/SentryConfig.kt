package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("sentry")
object SentryConfig {
    @ConfigurationProperty("dsn")
    var dsn: String? = null
}