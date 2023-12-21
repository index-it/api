package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("postgres")
object PostgresConfig {
    @ConfigurationProperty("url")
    lateinit var url: String

    @ConfigurationProperty("user")
    lateinit var user: String

    @ConfigurationProperty("password")
    lateinit var password: String
}
