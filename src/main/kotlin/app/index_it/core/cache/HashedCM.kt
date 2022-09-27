package app.index_it.core.cache

import app.index_it.core.clients.ObjectMapper
import app.index_it.core.clients.RedisClient


abstract class HashedCM(
    val keyName: String
) {

    inline fun <reified T> getValue(field: String): T? {
        RedisClient.jedisPool.resource.use {
            val json = it.hget(keyName, field)
            return if (json != null) ObjectMapper.decode(json) else null
        }
    }

    fun cacheValue(field: String, value: Any) {
        RedisClient.jedisPool.resource.use {
            val json = ObjectMapper.encode(value)
            it.hset(keyName, field, json)
        }
    }

    fun uncacheValue(field: String) {
        RedisClient.jedisPool.resource.use {
            it.hdel(keyName, field)
        }
    }
}
