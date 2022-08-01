package app.index_it.core.clients

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

object ObjectMapper {
    val json = Json { serializersModule = IdKotlinXSerializationModule }

    fun encode(data: Any): String {
        return json.encodeToString(data)
    }

    inline fun <reified T> decode(serializedData: String): T {
        return json.decodeFromString(serializedData)
    }
}
