package com.index

import io.github.cdimascio.dotenv.dotenv

object Env {
    private val dotenv = dotenv()

    lateinit var mongo_connection_string: String
    lateinit var mongo_db_name: String
    lateinit var redis_host: String
    var redis_port: Int = 6739

    object Jwt {
        lateinit var private_key: String
        lateinit var issuer: String
        lateinit var audience: String
        lateinit var realm: String
    }

    /**
     * @throws NoSuchElementException if a key isn't found in the .env file
     */
    fun loadEnv() {
        mongo_connection_string = getString("mongo.connection.string")
        mongo_db_name = getString("mongo.db.name")
        redis_host = getString("redis.host")
        redis_port = getInt("redis.port")

        Jwt.private_key = getString("jwt.private.key")
        Jwt.issuer = getString("jwt.issuer")
        Jwt.audience = getString("jwt.audience")
        Jwt.realm = getString("jwt.realm")
    }

    private fun getString(key: String) =
        dotenv[key.uppercase().replace(".", "_")]

    private fun getInt(key: String) : Int = try {
        getString(key).toInt()
    } catch (e: NumberFormatException) {
        throw NoSuchElementException("Couldn't find any $key INTEGER key in .env file")
    }
}
