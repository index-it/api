package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("sentry")
object SentryConfig {
    @ConfigurationProperty("dsn")
    var dsn: String? = null
}