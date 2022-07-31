package app.index_it

import io.github.cdimascio.dotenv.dotenv

object Env {
    private val dotenv = dotenv()

    var local_mode: Boolean = false

    var ktor_port: Int = 8080

    lateinit var mongo_connection_string: String
    lateinit var mongo_db_name: String
    lateinit var redis_host: String
    var redis_port: Int = 6739

    /**
     * @throws NoSuchElementException if a key isn't found in the .env file
     */
    fun loadEnv() {
        local_mode = getBoolean("local.mode")
        ktor_port = getInt("ktor.port")
        mongo_connection_string = getString("mongo.connection.string")
        mongo_db_name = getString("mongo.db.name")
        redis_host = getString("redis.host")
        redis_port = getInt("redis.port")
    }

    private fun getString(key: String) =
        dotenv[key.uppercase().replace(".", "_")]

    private fun getInt(key: String) : Int = try {
        getString(key).toInt()
    } catch (e: NumberFormatException) {
        throw NoSuchElementException("Couldn't find any $key INTEGER key in .env file")
    }

    private fun getBoolean(key: String) = getString(key).toBoolean()
}
