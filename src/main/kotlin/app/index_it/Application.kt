package app.index_it

import io.ktor.server.application.Application
import app.index_it.plugins.*
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
    // Disable warning logs from mongodb java driver (unnecessary)
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    loggerContext.getLogger("org.mongodb.driver").level = Level.WARN

    try {
        Env.loadEnv()
    } catch (e: NoSuchElementException) {
        log.error(e)
        exitProcess(404)
    }

    embeddedServer(Netty, port = Env.port, host = "0.0.0.0", module = Application::indexApplicationModule).start(wait = true)
}

fun Application.indexApplicationModule() {
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureValidator()
    configureStatusPages()
    // TODO: Rate limits
    configureRouting()
}
