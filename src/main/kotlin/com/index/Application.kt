package com.index

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.index.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureAdministration()
        configureRouting()
        configureSockets()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
    }.start(wait = true)
}
