package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("redis")
object RedisConfig {
    @ConfigurationProperty("connection.string")
    lateinit var connectionString: String
}
