package app.index_it.core.clients

import app.index_it.Env
import redis.clients.jedis.JedisPool

object RedisClient {
    val jedisPool = JedisPool(Env.redis_connection_string)
}
