package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("oauth")
object OAuthConfig {
    @ConfigurationProperty("google.client.id")
    lateinit var googleClientId: String

    @ConfigurationProperty("google.client.secret")
    lateinit var googleClientSecret: String

    @ConfigurationProperty("google.redirect.uri")
    lateinit var googleRedirectUri: String

    @ConfigurationProperty("apple.client.id")
    lateinit var appleClientId: String

    @ConfigurationProperty("apple.client.secret")
    lateinit var appleClientSecret: String

    @ConfigurationProperty("apple.redirect.uri")
    lateinit var appleRedirectUri: String

    @ConfigurationProperty("facebook.client.id")
    lateinit var facebookClientId: String

    @ConfigurationProperty("facebook.client.secret")
    lateinit var facebookClientSecret: String

    @ConfigurationProperty("facebook.redirect.uri")
    lateinit var facebookRedirectUri: String
}
