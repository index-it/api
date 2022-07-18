package com.index

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.index.plugins.*
import com.index.plugins.routing.configureRouting
import io.ktor.util.logging.*
import mu.KotlinLogging
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {  }

fun main() {
    try {
        Env.loadEnv()
    } catch (e: NoSuchElementException) {
        log.error(e)
        exitProcess(404)
    }

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
