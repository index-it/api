package app.index_it

import io.ktor.server.application.Application
import app.index_it.api.plugins.*
import app.index_it.api.routing.configureRouting
import app.index_it.core.clients.GoogleOAuthClient
import app.index_it.core.clients.MongoClient
import app.index_it.core.clients.RedisClient
import app.index_it.core.clients.SendinblueClient
import ch.qos.logback.classic.Level
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess
import ch.qos.logback.classic.Logger

private val log = KotlinLogging.logger { }

fun main() {
    /**
     * CONFIGURE LOGGING
     */
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.convertAnSLF4JLevel(Env.log_level)

    KotlinLogging

    /**
     * CONFIGURE ENVIRONMENT
     */
    try {
        Env.loadEnv()
    } catch (e: NoSuchElementException) {
        log.error(e)
        exitProcess(404)
    }

    /**
     * CONFIGURE SHUTDOWN HOOK
     */
    Runtime.getRuntime().addShutdownHook(
        Thread {
            SendinblueClient.close()
            GoogleOAuthClient.close()
            RedisClient.close()
            MongoClient.close()
        }
    )

    /**
     * READY TO LAUNCH? LAUNCH!
     */
    embeddedServer(Netty, port = Env.port, host = "0.0.0.0", module = Application::indexApplicationModule).start(wait = true)
}

private fun Application.indexApplicationModule() {
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureValidator()
    configureRouting()
}
