package com.index.core.cache

import com.index.core.clients.ObjectMapper
import com.index.core.clients.RedisClient


abstract class HashedCM(
    val hashName: String
) {

    inline fun <reified T> getValue(key: String): T? {
        RedisClient.jedisPool.resource.use {
            val json = it.hget(hashName, key)
            return if (json != null) ObjectMapper.decode(json) else null
        }
    }

    fun cacheValue(key: String, value: Any) {
        RedisClient.jedisPool.resource.use {
            val json = ObjectMapper.encode(value)
            it.hset(hashName, key, json)
        }
    }

    fun uncacheValue(key: String) {
        RedisClient.jedisPool.resource.use {
            it.hdel(hashName, key)
        }
    }
}
