package app.index_it

import app.index_it.plugins.*
import app.index_it.plugins.configureRouting
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import io.github.cdimascio.dotenv.DotenvException
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {  }

fun main() {
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    loggerContext.getLogger("org.mongodb.driver").level = Level.WARN

    try {
        Env.loadEnv()
    } catch (e: NoSuchElementException) {
        log.error(e)
        exitProcess(404)
    }

    embeddedServer(Netty, port = Env.ktor_port, host = "0.0.0.0") {
        configureAdministration()
        configureRouting()
        configureSockets()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
    }.start(wait = true)
}
