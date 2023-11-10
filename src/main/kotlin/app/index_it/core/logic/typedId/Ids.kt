package app.index_it.core.logic.typedId

import app.index_it.core.logic.typedId.impl.IxId
import java.util.*

fun <T> newIxId() = IxId<T>(UUID.randomUUID())