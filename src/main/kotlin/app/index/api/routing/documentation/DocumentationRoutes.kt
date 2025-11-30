import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Route.documentationRoutes() {
    swaggerUI("/docs/swagger", swaggerFile = "documentation/openapi.json")
    openAPI("/docs/openapi", swaggerFile = "documentation/openapi.json")
}