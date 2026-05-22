package app.index.api.plugins

import app.index.shared.core.typedId.serialization.IdKotlinXSerializationModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = IdKotlinXSerializationModule
                ignoreUnknownKeys = true
                encodeDefaults = true
            },
        )
    }
}
