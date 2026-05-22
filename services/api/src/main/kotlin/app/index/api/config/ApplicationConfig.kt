package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty
import app.index.api.config.core.models.ApplicationEnvironment
import ch.qos.logback.classic.Level

@Configuration("application")
object ApplicationConfig {
    @ConfigurationProperty("log.level")
    var logLevel: Level = Level.INFO

    @ConfigurationProperty("environment")
    var environment: ApplicationEnvironment = ApplicationEnvironment.LOCAL
}
