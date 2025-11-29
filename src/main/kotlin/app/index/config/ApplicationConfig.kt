package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty
import app.index.config.core.models.ApplicationEnvironment
import ch.qos.logback.classic.Level

@Configuration("application")
object ApplicationConfig {
    @ConfigurationProperty("log.level")
    var logLevel: Level = Level.INFO

    @ConfigurationProperty("environment")
    var environment: ApplicationEnvironment = ApplicationEnvironment.LOCAL
}
