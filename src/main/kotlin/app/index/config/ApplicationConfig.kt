package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty
import ch.qos.logback.classic.Level

@Configuration("application")
object ApplicationConfig {
    @ConfigurationProperty("log.level")
    var logLevel: Level = Level.INFO
}
