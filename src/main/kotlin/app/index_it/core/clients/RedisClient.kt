package app.index_it.core.clients

import app.index_it.config.RedisConfig
import app.index_it.di.IClosableComponent
import org.koin.core.annotation.Single
import redis.clients.jedis.JedisPool

@Single(createdAtStart = true)
class RedisClient : IClosableComponent {
    val jedisPool = JedisPool(RedisConfig.connectionString)

    override fun close() {
        jedisPool.close()
    }
}
