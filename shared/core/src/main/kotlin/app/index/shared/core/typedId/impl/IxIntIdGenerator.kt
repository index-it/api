package app.index.shared.core.typedId.impl

import app.index.shared.core.typedId.Id
import app.index.shared.core.typedId.IdGenerator
import org.koin.core.annotation.Factory
import kotlin.reflect.KClass

/**
 * Generator of [IxIntId] based on [Int].
 */
@Factory
class IxIntIdGenerator : IdGenerator {
    override val idClass: KClass<out Id<*>> = IxIntId::class

    override val wrappedIdClass: KClass<out Any> = Int::class

    @Deprecated("DO NOT USE")
    override fun <T> generateNewId(): Id<T> = IxIntId(0)
}
