package app.index.core.clients

import app.index.config.RedisConfig
import app.index.di.IClosableComponent
import org.koin.core.annotation.Single
import redis.clients.jedis.JedisPool

@Single(createdAtStart = true)
class RedisClient : IClosableComponent {
    val jedisPool = JedisPool(RedisConfig.connectionString)

    override fun close() {
        jedisPool.close()
    }
}
