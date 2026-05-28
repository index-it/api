package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty
import app.index.shared.core.config.core.models.ApplicationEnvironment
import ch.qos.logback.classic.Level

@Configuration("application")
object ApplicationConfig {
    @ConfigurationProperty("log.level")
    var logLevel: Level = Level.INFO

    @ConfigurationProperty("environment")
    var environment: ApplicationEnvironment = ApplicationEnvironment.LOCAL
}
