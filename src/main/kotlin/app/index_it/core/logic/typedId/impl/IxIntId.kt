package app.index_it.core.logic.typedId.impl

import app.index_it.core.logic.typedId.Id
import java.util.*

/**
 * A [UUID] id.
 */
data class IxIntId<T>(val id: Int) : Id<T> {

    constructor(id: String) : this(id.toInt())

    override fun toString(): String {
        return id.toString()
    }
}
