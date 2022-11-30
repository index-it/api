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

    var local_mode: Boolean = false

    lateinit var full_access_api_key: String
    lateinit var website_access_api_key: String

    lateinit var sendinblue_api_key: String

    lateinit var mongo_connection_string: String
    lateinit var mongo_db_name: String
    lateinit var redis_connection_string: String

    /**
     * @throws NoSuchElementException if a key isn't found in the .env file
     */
    fun loadEnv() {
        full_access_api_key = getString("full.access.api.key")
        website_access_api_key = getString("website.access.api.key")
        sendinblue_api_key = getString("sendinblue.api.key")
        local_mode = getBoolean("local.mode")
        mongo_connection_string = getString("mongo.connection.string")
        mongo_db_name = getString("mongo.db.name")
        redis_connection_string = getString("redis.connection.string")
    }

    private fun getString(key: String) : String {
        val formattedKey = key.uppercase().replace(".", "_")
        return dotenv?.get(formattedKey)
            ?: System.getenv(formattedKey)
            ?: throw NoSuchElementException("Couldn't find any $key key in .env file")
    }


    private fun getInt(key: String) : Int = try {
        getString(key).toInt()
    } catch (e: NumberFormatException) {
        throw NoSuchElementException("Couldn't find any $key INTEGER key in .env file")
    }

    private fun getBoolean(key: String) = getString(key).toBoolean()
}
