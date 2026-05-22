package app.index.api.core.logic.typedId

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.core.logic.typedId.impl.IxIntId
import java.util.*

/**
 * @throws IllegalArgumentException if the string is not a valid UUID
 */
fun <T> String.toIxId() = IxId<T>(this)

@Suppress("UNUSED")
fun <T> Int.toIxIntId() = IxIntId<T>(this)

fun <T> newIxId() = IxId<T>(UUID.randomUUID())

fun <T> newIxIntId() = IxIntId<T>((1..100).random())
