package app.index_it.api.plugins

import app.index_it.Env
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Env.log_level
    }
}
