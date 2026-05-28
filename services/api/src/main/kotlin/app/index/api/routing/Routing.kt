package app.index.api.routing

import app.index.shared.core.typedId.serialization.IdKotlinXSerializationModule
import app.index.api.routing.admin.adminRoutes
import app.index.api.routing.auth.authRoutes
import app.index.api.routing.documentation.documentationRoutes
import app.index.api.routing.kube.kubeRoutes
import app.index.api.routing.list.listRoutes
import app.index.api.routing.pro.proRoutes
import app.index.api.routing.task.taskRoutes
import app.index.api.routing.user.userRoutes
import app.index.api.routing.web.webRoutes
import app.index.api.routing.websocket.websocketRoutes
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // Needed for typed queries
    install(Resources) {
        serializersModule = IdKotlinXSerializationModule
    }

    routing {
        documentationRoutes()
        kubeRoutes()
        adminRoutes()
        webRoutes()
        authRoutes()
        userRoutes()
        listRoutes()
        taskRoutes()
        proRoutes()
        websocketRoutes()
    }
}
