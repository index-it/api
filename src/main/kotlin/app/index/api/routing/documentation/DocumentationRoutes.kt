package app.index.api.routing.documentation

import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.custom.IxRouteAttributeKey
import app.index.config.ApplicationConfig
import app.index.config.core.models.ApplicationEnvironment
import com.scalar.maven.core.ScalarHtmlRenderer
import com.scalar.maven.core.ScalarProperties
import com.scalar.maven.core.config.ScalarAgentOptions
import com.scalar.maven.core.enums.OperationTitleSource
import com.scalar.maven.core.enums.ScalarLayout
import com.scalar.maven.core.enums.ScalarTheme
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*


private val openApiInfo = OpenApiInfo(
    title = "Index API",
    termsOfService = "https://index-it.app/terms",
    contact = OpenApiInfo.Contact(email = "hello@index-it.app"),
    version = "1.0"
)


private fun baseScalarProperties() = ScalarProperties().apply {
    layout = ScalarLayout.MODERN
    theme = ScalarTheme.BLUE_PLANET
    agent = ScalarAgentOptions().apply { disabled = true }
    operationTitleSource = OperationTitleSource.PATH
}

private val scalarProperties = baseScalarProperties().apply {
    url = "/docs/swagger/documentation.yaml"
    path = "/docs/scalar"
    pageTitle = "Index API documentation"
}

private val scalarInternalProperties = baseScalarProperties().apply {
    url = "/docs/internal/swagger/documentation.yaml"
    path = "/docs/internal/scalar"
    pageTitle = "Index API internal documentation"
}


fun Route.documentationRoutes() {

    // doesn't work properly yet
//    get("/docs/openapi.json") {
//        val doc = OpenApiDoc(info = openApiInfo) + call.application.routingRoot.descendants()
//        call.respond(doc)
//    }.hide()


    //////////////
    /// PUBLIC ///
    //////////////

    swaggerUI("/docs/swagger") {
        info = openApiInfo
        servers {
            server("https://api.index-it.app") {
                description = "Production API server"
            }

            if (ApplicationConfig.environment == ApplicationEnvironment.LOCAL) {
                server("http://localhost:8080") {
                    description = "Local API server"
                }
            }
        }
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants().filter {
                !it.attributes.contains(IxRouteAttributeKey.Internal.attributeKey)
                        && it.children.none { child ->
                            child.attributes.contains(IxRouteAttributeKey.Internal.attributeKey)
                                    || child.children.any { childOfChild -> childOfChild.attributes.contains(IxRouteAttributeKey.Internal.attributeKey) }
                        }
            }
        }
    }.hide()

    get("/docs/scalar") {
        call.respondText(
            text = ScalarHtmlRenderer.render(scalarProperties),
            contentType = ContentType.Text.Html,
            status = HttpStatusCode.OK
        )
    }.hide()

    get("/docs/scalar/scalar.js") {
        call.respondBytes(
            bytes = ScalarHtmlRenderer.getScalarJsContent(),
            contentType = ContentType.Application.JavaScript,
            status = HttpStatusCode.OK
        )
    }.hide()


    ////////////////
    /// INTERNAL ///
    ////////////////

    authenticate(AuthenticationMethods.INTERNAL_OPENAPI_DOCS) {
        swaggerUI("/docs/internal/swagger") {
            info = openApiInfo
            servers {
                server("https://api.index-it.app") {
                    description = "Production API server"
                }

                if (ApplicationConfig.environment == ApplicationEnvironment.LOCAL) {
                    server("http://localhost:8080") {
                        description = "Local API server"
                    }
                }
            }
            source = OpenApiDocSource.Routing(ContentType.Application.Json) {
                routingRoot.descendants()
            }
        }.hide()

        get("/docs/internal/scalar") {
            call.respondText(
                text = ScalarHtmlRenderer.render(scalarInternalProperties),
                contentType = ContentType.Text.Html,
                status = HttpStatusCode.OK
            )
        }.hide()
    }

    get("/docs/internal/scalar/scalar.js") {
        call.respondBytes(
            bytes = ScalarHtmlRenderer.getScalarJsContent(),
            contentType = ContentType.Application.JavaScript,
            status = HttpStatusCode.OK
        )
    }.hide()
}