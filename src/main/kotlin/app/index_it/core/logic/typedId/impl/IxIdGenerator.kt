package app.index_it.core.logic.typedId.impl

import app.index_it.core.logic.typedId.Id
import app.index_it.core.logic.typedId.IdGenerator
import java.util.*
import kotlin.reflect.KClass


/**
 * Generator of [IxId] based on [UUID].
 */
object IxIdGenerator : IdGenerator {

    override val idClass: KClass<out Id<*>> = IxId::class

    override val wrappedIdClass: KClass<out Any> = UUID::class

    override fun <T> generateNewId(): Id<T> = IxId(UUID.randomUUID())
}