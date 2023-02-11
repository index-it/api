package app.index_it

import io.ktor.server.application.Application
import app.index_it.api.plugins.*
import app.index_it.api.routing.configureRouting
import app.index_it.core.clients.MongoClient
import app.index_it.core.clients.RedisClient
import app.index_it.core.clients.SendinblueClient
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val log = KotlinLogging.logger { }

fun main() {
    /**
     * CONFIGURE LOGGING
     */
    // Disable warning logs from mongodb java driver (unnecessary)
    // val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    // loggerContext.getLogger("org.mongodb.driver").level = Level.INFO

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
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureValidator()
    configureStatusPages()
    // TODO: Rate limits
    configureRouting()
}
