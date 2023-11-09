package app.index_it.core.cache.core

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper

/**
 * Hashed cache manager that accepts a dynamic values as hash keys
 *
 * Allows to have another layer of division of the hash.
 *
 * For example:
 * ```
 * --> base_{dynamic_1}
 *     |--> {field_1}
 *          |--> data
 *     |--> {field_2}
 *          |--> data
 * --> base_{dynamic_2}
 *     |--> {field_1}
 *          |--> data
 *     |--> {field_2}
 *          |--> data
 * ```
 *
 * @param keyBase the base for all the hash keys
 */
abstract class DoubleHashedCM(
    private val keyBase: String
) {
    /**
     * Constructs the hash key from the base + dynamic
     */
    protected fun keyName(keyValue: String) = "${keyBase}:$keyValue"

    /**
     * Get all the fields of a specific key of the hash
     */
    protected inline fun <reified T> getAll(keyValue: String): List<T> {
        RedisClient.jedisPool.resource.use {
            return ObjectMapper.decodeList(it.hgetAll(keyName(keyValue)).values)
        }
    }

    /**
     * Get a single field from the hash
     */
    protected inline fun <reified T> get(keyValue: String, field: String): T? {
        RedisClient.jedisPool.resource.use {
            val json = it.hget(keyName(keyValue), field)
            return if (json != null) ObjectMapper.decode(json) else null
        }
    }

    /**
     * Cache all the provided fields in the hash
     *
     * @param keyValue Value of the hash key
     * @param fieldToDataMap Map of field name to field data
     */
    protected inline fun <reified T> cacheAll(keyValue: String, fieldToDataMap: Map<String, T>) {
        RedisClient.jedisPool.resource.use {
            val jsonMap = fieldToDataMap.mapValues { mapItem -> ObjectMapper.encode(mapItem.value) }
            it.hset(keyName(keyValue), jsonMap)
        }
    }

    /**
     * Cache data in a field of the hash
     *
     * @param keyValue Value of the hash key
     * @param field Field for the value
     * @param data Data to cache
     */
    protected inline fun <reified T> cache(keyValue: String, field: String, data: T) {
        RedisClient.jedisPool.resource.use {
            val json = ObjectMapper.encode(data)
            it.hset(keyName(keyValue), field, json)
        }
    }

    /**
     * Delete a field from the hash
     * @param keyValue Value of the hash key
     * @param field Field to delete
     */
    protected fun delete(keyValue: String, field: String) {
        RedisClient.jedisPool.resource.use {
            it.hdel(keyName(keyValue), field)
        }
    }

    /**
     * Delete multiple fields from the hash
     * @param keyValue Value of the hash key
     * @param fields Fields to delete
     */
    protected fun deleteMultiple(keyValue: String, vararg fields: String) {
        if (fields.isNotEmpty()) {
            RedisClient.jedisPool.resource.use {
                it.hdel(keyName(keyValue), *fields)
            }
        }
    }

    /**
     * Delete all fields from the hash
     * @param keyValue Value of the hash key
     */
    protected fun deleteAll(keyValue: String) {
        RedisClient.jedisPool.resource.use {
            it.del(keyName(keyValue))
        }
    }
}
