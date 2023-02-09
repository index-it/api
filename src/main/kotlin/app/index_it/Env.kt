package app.index_it

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

// TODO: Use a config store in production
object Env {
    private val dotenv: Dotenv? = try {
        dotenv()
    } catch (_: Exception) {
        log.warn(".env file not found, using System variables")
        null
    }

    lateinit var cors_host: String
    var secure_cookies: Boolean = true

    lateinit var full_access_api_key: String

    lateinit var sendinblue_api_key: String

    lateinit var mongo_connection_string: String
    lateinit var mongo_db_name: String
    lateinit var redis_connection_string: String


    fun loadEnv() {
        cors_host = getStringFromEnv("cors.host")
        secure_cookies = getBooleanFromEnv("cookie_secure")
        full_access_api_key = getStringFromEnv("full.access.api.key")
        sendinblue_api_key = getStringFromEnv("sendinblue.api.key")
        mongo_connection_string = getStringFromEnv("mongo.connection.string")
        mongo_db_name = getStringFromEnv("mongo.db.name")
        redis_connection_string = getStringFromEnv("redis.connection.string")
    }

    private fun getStringFromEnv(key: String) : String {
        val formattedKey = key.uppercase().replace(".", "_")
        return dotenv?.get(formattedKey)
            ?: System.getenv(formattedKey)
            ?: throw NoSuchElementException("Couldn't find any $key key in .env file")
    }


    private fun getIntFromEnv(key: String) : Int = try {
        getStringFromEnv(key).toInt()
    } catch (e: NumberFormatException) {
        throw NoSuchElementException("Couldn't find any $key INTEGER key in .env file")
    }

    private fun getBooleanFromEnv(key: String) = getStringFromEnv(key).toBoolean()
}
