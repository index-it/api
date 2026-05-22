package app.index.api.plugins.custom

import io.ktor.server.routing.*
import io.ktor.util.*

enum class IxRouteAttributeKey(val key: String) {
    Internal("Internal");

    val attributeKey: AttributeKey<Unit> = AttributeKey(key)
}

/**
 * Adds metadata to [Route.attributes]
 *
 * @param keys: the attributes to set for the route
 *
 * @return the current route for chaining expressions
 */
fun Route.meta(keys: Set<IxRouteAttributeKey>): Route {
    keys.forEach {  key ->
        attributes[key.attributeKey] = Unit
    }
    return this
}

fun Route.internal(): Route {
    return meta(setOf(IxRouteAttributeKey.Internal))
}

