package app.index.api.routing

import app.index.api.routing.admin.adminRoutes
import app.index.api.routing.auth.authRoutes
import app.index.api.routing.kube.kubeRoutes
import app.index.api.routing.list.listRoutes
import app.index.api.routing.monitoring.monitoringRoutes
import app.index.api.routing.pro.proRoutes
import app.index.api.routing.suggestion.suggestionRoutes
import app.index.api.routing.task.taskRoutes
import app.index.api.routing.user.userRoutes
import app.index.api.routing.web.webRoutes
import app.index.api.routing.websocket.websocketRoutes
import app.index.core.logic.typedId.serialization.IdKotlinXSerializationModule
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
        proRoutes()
        websocketRoutes()
    }
}
