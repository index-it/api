package com.index.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            serializersModule = IdKotlinXSerializationModule
        })
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
