package app.index

import app.index.api.plugins.*
import app.index.api.routing.configureRouting
import app.index.config.ApiConfig
import app.index.config.ApplicationConfig
import app.index.config.SentryConfig
import app.index.config.core.ConfigurationManager
import app.index.config.core.ConfigurationReader
import app.index.config.core.models.ApplicationEnvironment
import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.sentry.Sentry
import org.slf4j.LoggerFactory

fun main() {
    /**
     * Load configuration properties (environment)
     */
    val configInitializer =
        ConfigurationManager(
            packageName = ConfigurationManager.DEFAULT_CONFIG_PACKAGE,
            ConfigurationReader::read,
        )

    configInitializer.initialize()

    /**
     * Configure Sentry environment
     */
    val isSentryEnabled = ApplicationConfig.environment != ApplicationEnvironment.LOCAL

    Sentry.init { options ->
        options.environment = ApplicationConfig.environment.sentryName
        options.isEnabled = isSentryEnabled
        options.dsn = if (isSentryEnabled) SentryConfig.dsn else null
    }

    /**
     * Configure logging
     */
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = ApplicationConfig.logLevel

    /**
     * Start api server
     */
    embeddedServer(Netty, port = ApiConfig.port, host = "0.0.0.0", module = Application::indexApplicationModule)
        .start(wait = true)
}

private fun Application.indexApplicationModule() {
    configureDI()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureValidator()
    configureWebsockets()
    configureRouting()
    configureSwagger()
}
