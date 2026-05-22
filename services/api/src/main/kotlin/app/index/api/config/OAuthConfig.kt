package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("oauth")
object OAuthConfig {
    @ConfigurationProperty("google.client.id")
    lateinit var googleClientId: String
}
