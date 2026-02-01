package app.index.api.routing.documentation

import io.ktor.http.ContentType
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.OpenApiDocSource

fun Route.documentationRoutes() {
    swaggerUI("/docs/swagger") {
        // TODO: Improve
        info = OpenApiInfo("Index API", "1.0")
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants()
        }
    }

    openAPI(path = "/docs/openapi") {
        info = OpenApiInfo("Index API", "1.0")
        source = OpenApiDocSource.Routing {
            routingRoot.descendants()
        }
    }
}