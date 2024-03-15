package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("api")
object ApiConfig {
    @ConfigurationProperty("port")
    var port: Int = 8080

    @ConfigurationProperty("cookie.secure")
    var cookieSecure: Boolean = true

    @ConfigurationProperty("session.max.age.in.seconds")
    var sessionMaxAgeInSeconds: Long = 2592000 // 30 days by default

    @ConfigurationProperty("admin.key")
    var adminKey = "MbeoAzYNdQPnT5gF"
}
