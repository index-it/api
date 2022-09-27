package app.index_it.core.cache

import app.index_it.core.clients.ObjectMapper
import app.index_it.core.clients.RedisClient

abstract class DoubleHashedCM(
    val keyBase: String
) {
    fun keyName(hashValue: String) = "${keyBase}_$hashValue"

    inline fun <reified T> getAllValues(keyValue: String): List<T> {
        RedisClient.jedisPool.resource.use {
            return ObjectMapper.decodeList(it.hgetAll(keyName(keyValue)).values)
        }
    }

    inline fun <reified T> getValue(keyValue: String, field: String): T? {
        RedisClient.jedisPool.resource.use {
            val json = it.hget(keyName(keyValue), field)
            return if (json != null) ObjectMapper.decode(json) else null
        }
    }

    fun cacheAllValues(keyValue: String, fieldValueMap: Map<String, Any>) {
        RedisClient.jedisPool.resource.use {
            val jsonMap = fieldValueMap.mapValues { mapItem -> ObjectMapper.encode(mapItem.value) }
            it.hset(keyName(keyValue), jsonMap)
        }
    }

    fun cacheValue(keyValue: String, field: String, value: Any) {
        RedisClient.jedisPool.resource.use {
            val json = ObjectMapper.encode(value)
            it.hset(keyName(keyValue), field, json)
        }
    }

    fun uncacheValue(keyValue: String, field: String) {
        RedisClient.jedisPool.resource.use {
            it.hdel(keyName(keyValue), field)
        }
    }
}
