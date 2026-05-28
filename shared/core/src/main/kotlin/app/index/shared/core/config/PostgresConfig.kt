package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("postgres")
object PostgresConfig {
    @ConfigurationProperty("url")
    var url: String = "jdbc:postgresql://localhost:5432/indexdevdb"

    @ConfigurationProperty("user")
    var user: String = "IndexDevUser"

    @ConfigurationProperty("password")
    var password: String = "IndexDevPassword"
}
