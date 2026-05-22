package app.index.api.core.clients

import app.index.api.config.RedisConfig
import app.index.api.di.IClosableComponent
import org.koin.core.annotation.Single
import redis.clients.jedis.JedisPool

@Single(createdAtStart = true)
class RedisClient : IClosableComponent {
    val jedisPool = JedisPool(RedisConfig.connectionString)

    override suspend fun close() {
        jedisPool.close()
    }
}
