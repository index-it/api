package app.index.shared.core.data.sources.cache.core

import app.index.shared.core.clients.RedisClient
import app.index.shared.core.logic.ObjectMapper

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
    val keyName: String,
    val redisClient: RedisClient,
    val objectMapper: ObjectMapper,
) {
    /**
     * Get a single field from the hash
     */
    protected inline fun <reified T> get(field: String): T? {
        redisClient.jedisPool.resource.use {
            val json = it.hget(keyName, field)
            return if (json != null) objectMapper.decode(json) else null
        }
    }

    /**
     * Cache data in a field of the hash
     *
     * @param field Field where the data will be cached
     * @param data Data to cache
     */
    protected inline fun <reified T> cache(
        field: String,
        data: T,
    ) {
        redisClient.jedisPool.resource.use {
            val json = objectMapper.encode(data)
            it.hset(keyName, field, json)
        }
    }

    /**
     * Delete a field from the hash
     */
    protected fun delete(field: String) {
        redisClient.jedisPool.resource.use {
            it.hdel(keyName, field)
        }
    }
}
