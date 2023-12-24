package app.index

import app.index.api.plugins.*
import app.index.api.routing.configureRouting
import app.index.config.ApiConfig
import app.index.config.ApplicationConfig
import app.index.config.core.ConfigurationManager
import app.index.config.core.ConfigurationReader
import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.time.Instant

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
