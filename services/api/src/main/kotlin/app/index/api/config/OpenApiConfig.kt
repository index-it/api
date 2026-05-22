package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

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