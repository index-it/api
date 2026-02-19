package app.index.api.routing.documentation

import io.ktor.http.ContentType
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.OpenApiDocSource

private val openApiInfo = OpenApiInfo(
    title = "Index API",
    termsOfService = "https://index-it.app/terms",
    contact = OpenApiInfo.Contact(email = "hello@index-it.app"),
    version = "1.0"
)

fun Route.documentationRoutes() {
    swaggerUI("/docs/swagger") {
        info = openApiInfo
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants()
        }
    }

    openAPI(path = "/docs/openapi") {
        info = openApiInfo
        source = OpenApiDocSource.Routing {
            routingRoot.descendants()
        }
    }
}