package app.index.shared.core.clients

import app.index.shared.core.config.RedisConfig
import app.index.shared.core.di.IClosableComponent
import org.koin.core.annotation.Single
import redis.clients.jedis.JedisPool

@Single(createdAtStart = true)
class RedisClient : IClosableComponent {
    val jedisPool = JedisPool(RedisConfig.connectionString)

    override suspend fun close() {
        jedisPool.close()
    }
}
