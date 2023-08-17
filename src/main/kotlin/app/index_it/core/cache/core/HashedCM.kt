package app.index_it.core.cache.core

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper

/**
 * Hashed cache manager
 *
 * Example:
 * ```
 * --> hash_key
 *     |--> {field_1}
 *          |--> data
 *     |--> {field_2}
 *          |--> data
 * ```
 *
 * @param keyName Name of the hash key
 */
abstract class HashedCM(
    val keyName: String
) {

    /**
     * Get a single field from the hash
     */
    inline fun <reified T> get(field: String): T? {
        RedisClient.jedisPool.resource.use {
            val json = it.hget(keyName, field)
            return if (json != null) ObjectMapper.decode(json) else null
        }
    }

    /**
     * Cache data in a field of the hash
     *
     * @param field Field where the data will be cached
     * @param data Data to cache
     */
    inline fun <reified T> cache(field: String, data: T) {
        RedisClient.jedisPool.resource.use {
            val json = ObjectMapper.encode(data)
            it.hset(keyName, field, json)
        }
    }

    /**
     * Delete a field from the hash
     */
    fun delete(field: String) {
        RedisClient.jedisPool.resource.use {
            it.hdel(keyName, field)
        }
    }
}
