package app.index_it.core.logic.typedId.serialization

import app.index_it.core.logic.typedId.Id
import app.index_it.core.logic.typedId.IdGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.impl.IxIdGenerator
import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.core.logic.typedId.impl.IxIntIdGenerator
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass


/**
 * The Id kotlin.x Serialization module.
 */
val IdKotlinXSerializationModule: SerializersModule by lazy {
    SerializersModule {
        contextual(Id::class, IdSerializer())
        contextual(IxId::class, IxIdSerializer())
        contextual(IxIntId::class, IxIntIdSerializer())
        if (IdGenerator.defaultGenerator.idClass != IxId::class
            && IdGenerator.defaultGenerator.idClass != IxId::class) {
            @Suppress("UNCHECKED_CAST")
            contextual(
                IdGenerator.defaultGenerator.idClass as KClass<Id<*>>,
                IdSerializer()
            )
        }
    }
}

private class IdSerializer<T : Id<*>> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IdSerializer", PrimitiveKind.STRING)

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): T =
        IdGenerator.defaultGenerator.create(decoder.decodeString()) as T

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.toString())
    }
}

private class IxIdSerializer<T : IxId<*>> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IdSerializer", PrimitiveKind.STRING)

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): T =
        IxIdGenerator.create(decoder.decodeString()) as T

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.toString())
    }
}

private class IxIntIdSerializer<T : IxIntId<*>> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IdSerializer", PrimitiveKind.STRING)

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): T =
        IxIntIdGenerator.create(decoder.decodeString()) as T

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.toString())
    }
}