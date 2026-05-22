package app.index.shared.core.typedId.impl

import app.index.shared.core.typedId.Id
import app.index.shared.core.typedId.IdGenerator
import org.koin.core.annotation.Factory
import java.util.*
import kotlin.reflect.KClass

/**
 * Generator of [IxId] based on [UUID].
 */
@Factory
class IxIdGenerator : IdGenerator {
    override val idClass: KClass<out Id<*>> = IxId::class

    override val wrappedIdClass: KClass<out Any> = UUID::class

    override fun <T> generateNewId(): Id<T> = IxId(UUID.randomUUID())
}
