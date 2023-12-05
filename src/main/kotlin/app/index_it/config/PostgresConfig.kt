package app.index_it.config

import app.index_it.config.core.Configuration
import app.index_it.config.core.ConfigurationProperty

@Configuration("postgres")
object PostgresConfig {
    @ConfigurationProperty("url")
    lateinit var url: String

    @ConfigurationProperty("user")
    lateinit var user: String

    @ConfigurationProperty("password")
    lateinit var password: String
}