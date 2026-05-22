package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

/**
 * Configuration for OpenApi docs
 */
@Configuration("openapi")
object OpenApiConfig {
    @ConfigurationProperty("internal.username")
    var internalUsername: String = "admin"

    @ConfigurationProperty("internal.password")
    lateinit var internalPassword: String
}