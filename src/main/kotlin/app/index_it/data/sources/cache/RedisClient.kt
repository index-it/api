package app.index_it.data.sources.cache

import app.index_it.config.RedisConfig
import redis.clients.jedis.JedisPool

object RedisClient {
    val jedisPool = JedisPool(RedisConfig.connectionString)

    fun close() {
        jedisPool.close()
    }
}
