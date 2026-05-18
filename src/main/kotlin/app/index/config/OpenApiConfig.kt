package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

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