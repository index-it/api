package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("sentry")
object SentryConfig {
    @ConfigurationProperty("dsn")
    var dsn: String? = null
}