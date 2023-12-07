package app.index_it.api.routing

import app.index_it.api.routing.admin.adminRoutes
import app.index_it.api.routing.auth.authRoutes
import app.index_it.api.routing.kube.kubeRoutes
import app.index_it.api.routing.list.listRoutes
import app.index_it.api.routing.monitoring.monitoringRoutes
import app.index_it.api.routing.suggestion.suggestionRoutes
import app.index_it.api.routing.task.taskRoutes
import app.index_it.api.routing.user.userRoutes
import app.index_it.api.routing.web.webRoutes
import app.index_it.api.routing.websocket.websocketRoutes
import app.index_it.core.logic.typedId.serialization.IdKotlinXSerializationModule
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // Needed for typed queries
    install(Resources) {
        serializersModule = IdKotlinXSerializationModule
    }

    routing {
        kubeRoutes()
        monitoringRoutes()
        adminRoutes()
        webRoutes()
        authRoutes()
        userRoutes()
        listRoutes()
        taskRoutes()
        suggestionRoutes()
        websocketRoutes()
    }
}
