package app.index_it.core.logic

import io.netty.handler.codec.protobuf.ProtobufEncoder
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf

object ObjectMapper {
    val json = Json {
        serializersModule = IdKotlinXSerializationModule
        prettyPrint = true
    }

    inline fun <reified T> encode(data: T): String {
        return json.encodeToString(data)
    }

    inline fun <reified T> encodeToByteArray(data: T): ByteArray {
        return json.encodeToString(data).encodeToByteArray()
    }

    inline fun <reified T> decode(serializedData: String): T {
        return json.decodeFromString(serializedData)
    }

    inline fun <reified T> decodeFromByteArray(serializedByteArray: ByteArray): T {
        return json.decodeFromString(serializedByteArray.decodeToString())
    }

    inline fun <reified T> decodeList(listOfData: MutableCollection<String>): List<T> {
        return json.decodeFromString("[${listOfData.joinToString(", ")}]")
    }
}
