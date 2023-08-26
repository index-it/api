package app.index_it.api.plugins

import app.index_it.Env
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.valueOf(Env.log_level.levelStr)
    }
}
