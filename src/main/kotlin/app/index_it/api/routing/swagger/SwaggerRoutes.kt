package app.index_it.api.routing.swagger

import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Route.swaggerRoutes() {
    swaggerUI(path = "swagger/internal", swaggerFile = "openapi/internal-openapi-documentation.yaml")
}
