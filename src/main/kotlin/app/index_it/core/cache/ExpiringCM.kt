package app.index_it.core.cache

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper

abstract class ExpiringCM(
    val keyBase: String,
    val expirationInSeconds: Long
) {
    fun keyName(hashValue: String) = "${keyBase}:$hashValue"

    inline fun <reified T> getValue(keyValue: String): T? {
        RedisClient.jedisPool.resource.use {
            val json = it.get(keyName(keyValue))
            return if (json != null) ObjectMapper.decode(json) else null
        }
    }

    inline fun <reified T> cacheValue(keyValue: String, value: T) {
        RedisClient.jedisPool.resource.use {
            val json = ObjectMapper.encode(value)
            it.setex(keyName(keyValue), expirationInSeconds, json)
        }
    }

    fun uncacheValue(keyValue: String) {
        RedisClient.jedisPool.resource.use {
            it.del(keyName(keyValue))
        }
    }
}
