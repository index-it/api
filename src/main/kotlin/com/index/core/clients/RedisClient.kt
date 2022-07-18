package com.index.core.clients

import com.index.Env
import redis.clients.jedis.JedisPool

object RedisClient {
    val jedisPool = JedisPool(Env.redis_host, Env.redis_port)
}
