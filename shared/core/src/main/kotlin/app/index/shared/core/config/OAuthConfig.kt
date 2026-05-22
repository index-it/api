package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("oauth")
object OAuthConfig {
    @ConfigurationProperty("google.client.id")
    lateinit var googleClientId: String
}
