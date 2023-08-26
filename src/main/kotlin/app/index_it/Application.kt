package app.index_it

import app.index_it.api.plugins.*
import app.index_it.api.routing.configureRouting
import app.index_it.core.clients.MongoClient
import app.index_it.core.clients.RabbitMqClient
import app.index_it.core.clients.RedisClient
import app.index_it.core.clients.SendinblueClient
import app.index_it.core.clients.oauth.AppleOAuthClient
import app.index_it.core.clients.oauth.FacebookOAuthClient
import app.index_it.core.logic.websocket.WebsocketConnectionsManager
import app.index_it.core.logic.websocket.WebsocketsQueueManager
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess
import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import org.slf4j.LoggerFactory

private val logger = KotlinLogging.logger { }

fun main() {
    /**
     * Load environment.
     */
    try {
        Env.loadEnv()
    } catch (e: NoSuchElementException) {
        logger.error { e }
        exitProcess(404)
    }

    /**
     * Configure logging
     */
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Env.log_level

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
        logger.info { "[1/8] Closing all websocket connections" }
        runBlocking {
            WebsocketConnectionsManager.close()
        }
        logger.info { "[1/8] All websocket connections have been closed" }
    }

    /**
     * Configure application shutdown hook
     */
    Runtime.getRuntime().addShutdownHook(
        Thread {
            logger.info { "Shutdown started" }

            logger.info { "[1/8] Api server shutdown" }

            SendinblueClient.close()
            logger.info { "[2/8] SendinblueClient client shutdown" }

            AppleOAuthClient.close()
            logger.info { "[3/8] AppleOAuthClient client shutdown" }

            FacebookOAuthClient.close()
            logger.info { "[4/8] FacebookOAuthClient client shutdown" }

            WebsocketsQueueManager.close()
            logger.info { "[5/8] WebsocketsQueueManager client shutdown" }

            RabbitMqClient.close()
            logger.info { "[6/8] RabbitMqClient client shutdown" }

            RedisClient.close()
            logger.info { "[7/8] RedisClient client shutdown" }

            MongoClient.close()
            logger.info { "[8/8] MongoClient client shutdown" }

            logger.info { "Shutdown successful, bye bye ^^" }
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
    configureSwagger()
}
