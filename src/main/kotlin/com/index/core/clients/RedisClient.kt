package com.index.core.clients

import com.index.Env
import com.index.models.user.UserDto
import com.index.models.user.UserLoginDto
import org.litote.kmongo.json
import redis.clients.jedis.JedisPool

object RedisClient {
    val jedisPool = JedisPool(Env.redis_host, Env.redis_port)
}
