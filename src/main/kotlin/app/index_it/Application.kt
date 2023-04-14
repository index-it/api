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
     * Load environment.
     */
    try {
        Env.loadEnv()
    } catch (e: NoSuchElementException) {
        log.error(e)
        exitProcess(404)
    }

    /**
     * Configure logging
     */
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.convertAnSLF4JLevel(Env.log_level)

    /**
     * Initialize objects
     * If objects aren't called they initialize lazily which can lead to a false positive ready state
     */
    MongoClient.init()
    RedisClient
    RabbitMqClient
    WebsocketsQueueManager.startListening()


    /**
     * Launch api server
     */
    val apiServer = embeddedServer(Netty, port = Env.port, host = "0.0.0.0", module = Application::indexApplicationModule)

    // Add shutdown hook to api server
    apiServer.addShutdownHook {
        log.info("[1/10] Closing all websocket connections")
        runBlocking {
            WebsocketConnectionsManager.close()
        }
        log.info("[1/10] All websocket connections have been closed")
    }

    /**
     * Configure application shutdown hook
     */
    Runtime.getRuntime().addShutdownHook(
        Thread {
            log.info("Shutdown started")

            log.info("[1/10] Api server shutdown")

            SendinblueClient.close()
            log.info("[2/10] SendinblueClient client shutdown")

            GoogleOAuthClient.close()
            log.info("[3/10] GoogleOAuthClient client shutdown")

            AppleOAuthClient.close()
            log.info("[4/10] AppleOAuthClient client shutdown")

            FacebookOAuthClient.close()
            log.info("[5/10] FacebookOAuthClient client shutdown")

            WebsocketsQueueManager.close()
            log.info("[6/10] WebsocketsQueueManager client shutdown")

            RabbitMqClient.close()
            log.info("[7/10] RabbitMqClient client shutdown")

            RedisClient.close()
            log.info("[8/10] RedisClient client shutdown")

            MongoClient.close()
            log.info("[9/10] MongoClient client shutdown")

            log.info("Shutdown successful, bye bye ^^")
        }
    )

    apiServer.start(wait = true)
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
