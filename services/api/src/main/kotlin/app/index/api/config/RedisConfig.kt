package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("redis")
object RedisConfig {
    @ConfigurationProperty("connection.string")
    var connectionString: String = "redis://localhost:6379"
}
