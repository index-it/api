package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("postgres")
object PostgresConfig {
    @ConfigurationProperty("url")
    var url: String = "jdbc:postgresql://localhost:5432/indexdevdb"

    @ConfigurationProperty("user")
    var user: String = "IndexDevUser"

    @ConfigurationProperty("password")
    var password: String = "IndexDevPassword"
}
