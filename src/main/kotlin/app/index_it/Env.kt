package app.index_it

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import mu.KotlinLogging
import org.slf4j.event.Level

private val log = KotlinLogging.logger {  }

object Env {
    private val dotenv: Dotenv? = try {
        dotenv()
    } catch (_: Exception) {
        log.warn(".env file not found, using System variables")
        null
    }

    var log_level: Level = Level.INFO

    var port: Int = 8080
    var cookie_secure: Boolean = true
    var session_max_age_in_seconds: Long = 604800 // 7 days by default

    lateinit var admin_api_key: String

    lateinit var sendinblue_api_key: String

    lateinit var mongo_connection_string: String
    lateinit var mongo_db_name: String

    lateinit var redis_connection_string: String

    lateinit var rabbitmq_connection_string: String
    lateinit var rabbitmq_exchange_name: String
    lateinit var rabbitmq_websockets_queue_name: String
    lateinit var rabbitmq_websockets_routing_key: String

    lateinit var email_verification_success_url: String
    lateinit var email_verification_error_url: String
    lateinit var email_verification_url: String
    lateinit var reset_password_url: String

    lateinit var google_client_id: String
    lateinit var google_client_secret: String
    lateinit var google_redirect_uri: String

    lateinit var apple_client_id: String
    lateinit var apple_client_secret: String
    lateinit var apple_redirect_uri: String

    lateinit var facebook_client_id: String
    lateinit var facebook_client_secret: String
    lateinit var facebook_redirect_uri: String

    fun loadEnv() {
        log_level = try {
            Level.valueOf(
                getStringFromEnv("log.level").uppercase()
            )
        } catch (_: IllegalArgumentException) {
            throw NoSuchElementException("Invalid LOG_LEVEL in environment")
        }

        port = getIntFromEnv("port")
        cookie_secure = getBooleanFromEnv("cookie.secure")
        session_max_age_in_seconds = getLongFromEnv("session.max.age.in.seconds")
        admin_api_key = getStringFromEnv("admin.api.key")
        sendinblue_api_key = getStringFromEnv("sendinblue.api.key")
        mongo_connection_string = getStringFromEnv("mongo.connection.string")
        mongo_db_name = getStringFromEnv("mongo.db.name")
        redis_connection_string = getStringFromEnv("redis.connection.string")

        rabbitmq_connection_string = getStringFromEnv("rabbitmq.connection.string")
        rabbitmq_exchange_name = getStringFromEnv("rabbitmq.exchange.name")
        rabbitmq_websockets_queue_name = getStringFromEnv("rabbitmq.websockets.queue.name")
        rabbitmq_websockets_routing_key = getStringFromEnv("rabbitmq.websockets.routing.key")

        email_verification_success_url = getStringFromEnv("email.verification.success.url")
        email_verification_error_url = getStringFromEnv("email.verification.error.url")
        email_verification_url = getStringFromEnv("email.verification.url")
        reset_password_url = getStringFromEnv("reset.password.url")

        google_client_id = getStringFromEnv("google.client.id")
        google_client_secret = getStringFromEnv("google.client.secret")
        google_redirect_uri = getStringFromEnv("google.redirect.uri")

        apple_client_id = getStringFromEnv("apple.client.id")
        apple_client_secret = getStringFromEnv("apple.client.secret")
        apple_redirect_uri = getStringFromEnv("apple.redirect.uri")

        facebook_client_id = getStringFromEnv("facebook.client.id")
        facebook_client_secret = getStringFromEnv("facebook.client.secret")
        facebook_redirect_uri = getStringFromEnv("facebook.redirect.uri")
    }

    @Suppress("SameParameterValue")
    private fun getStringFromEnv(key: String) : String {
        val formattedKey = key.uppercase().replace(".", "_")
        return dotenv?.get(formattedKey)
            ?: System.getenv(formattedKey)
            ?: throw NoSuchElementException("Couldn't find any $key key in environment")
    }


    @Suppress("SameParameterValue")
    private fun getIntFromEnv(key: String) : Int = try {
        getStringFromEnv(key).toInt()
    } catch (e: NumberFormatException) {
        throw NoSuchElementException("Couldn't find any $key INTEGER key in environment")
    }

    @Suppress("SameParameterValue")
    private fun getLongFromEnv(key: String) : Long = try {
        getStringFromEnv(key).toLong()
    } catch (e: NumberFormatException) {
        throw NoSuchElementException("Couldn't find any $key LONG key in environment")
    }

    @Suppress("SameParameterValue")
    private fun getBooleanFromEnv(key: String) = getStringFromEnv(key).toBoolean()
}
