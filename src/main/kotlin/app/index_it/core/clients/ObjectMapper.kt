package app.index_it.core.clients

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

object ObjectMapper {
    val json = Json {
        serializersModule = IdKotlinXSerializationModule
        prettyPrint = true
    }

    inline fun <reified T> encode(data: T): String {
        return json.encodeToString(data)
    }

    inline fun <reified T> decode(serializedData: String): T {
        return json.decodeFromString(serializedData)
    }

    inline fun <reified T> decodeList(listOfData: MutableCollection<String>): List<T> {
        return json.decodeFromString("[${listOfData.joinToString(", ")}]")
    }
}
