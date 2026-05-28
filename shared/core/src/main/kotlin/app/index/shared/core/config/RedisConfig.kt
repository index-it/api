package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("redis")
object RedisConfig {
    @ConfigurationProperty("connection.string")
    var connectionString: String = "redis://localhost:6379"
}
