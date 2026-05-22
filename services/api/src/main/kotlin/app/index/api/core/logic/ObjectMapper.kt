package app.index.api.core.logic

import app.index.shared.core.typedId.serialization.IdKotlinXSerializationModule
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import org.koin.core.annotation.Factory

@Factory
class ObjectMapper {
    val json = Json {
        serializersModule = IdKotlinXSerializationModule
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    val jsonOmittingClassDiscriminator = Json {
        serializersModule = IdKotlinXSerializationModule
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    inline fun <reified T> encode(data: T, omitClassDiscriminator: Boolean = false): String {
        val json = if (omitClassDiscriminator) jsonOmittingClassDiscriminator else json
        return json.encodeToString(data)
    }

    inline fun <reified T> encodeToByteArray(data: T, omitClassDiscriminator: Boolean = false): ByteArray {
        val json = if (omitClassDiscriminator) jsonOmittingClassDiscriminator else json
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


    /////////////////////
    /// MAP UTILITIES ///
    /////////////////////
    inline fun <reified T> encodeToMap(data: T, omitClassDiscriminator: Boolean = false): Map<String, Any?> {
        val json = if (omitClassDiscriminator) jsonOmittingClassDiscriminator else json
        return jsonObjectToMap(json.encodeToJsonElement(data).jsonObject)
    }

    fun jsonObjectToMap(element: JsonObject): Map<String, Any?> {
        return element.entries.associate {
            it.key to extractValue(it.value)
        }
    }

    private fun extractValue(element: JsonElement): Any? {
        return when (element) {
            is JsonNull -> null
            is JsonPrimitive -> element.content
            is JsonArray -> element.map { extractValue(it) }
            is JsonObject -> jsonObjectToMap(element)
        }
    }
}
