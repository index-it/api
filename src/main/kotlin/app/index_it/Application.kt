package app.index_it

import app.index_it.api.plugins.*
import app.index_it.api.routing.configureRouting
import app.index_it.core.clients.MongoClient
import app.index_it.core.clients.RabbitMqClient
import app.index_it.core.clients.RedisClient
import app.index_it.core.clients.SendinblueClient
import app.index_it.core.clients.oauth.AppleOAuthClient
import app.index_it.core.clients.oauth.FacebookOAuthClient
import app.index_it.core.clients.oauth.GoogleOAuthClient
import app.index_it.core.logic.websocket.WebsocketConnectionsManager
import app.index_it.core.logic.websocket.WebsocketsQueueManager
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

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
            // TODO: Shutdown api server
            SendinblueClient.close()
            GoogleOAuthClient.close()
            AppleOAuthClient.close()
            FacebookOAuthClient.close()
            WebsocketsQueueManager.close()
            runBlocking {
                WebsocketConnectionsManager.close()
            }
            RabbitMqClient.close()
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
    configureWebsockets()
    configureRouting()
}
