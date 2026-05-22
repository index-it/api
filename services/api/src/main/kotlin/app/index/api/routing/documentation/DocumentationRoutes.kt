package app.index.api.routing.documentation

import app.index.api.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.custom.IxRouteAttributeKey
import com.scalar.maven.core.ScalarHtmlRenderer
import com.scalar.maven.core.ScalarProperties
import com.scalar.maven.core.config.ScalarAgentOptions
import com.scalar.maven.core.enums.OperationTitleSource
import com.scalar.maven.core.enums.ScalarLayout
import com.scalar.maven.core.enums.ScalarTheme
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import kotlinx.serialization.json.Json


private val jsonEncoder = Json {
    serializersModule = IdKotlinXSerializationModule
    prettyPrint = true
    encodeDefaults = false
    ignoreUnknownKeys = true
}


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
    url = "/docs/openapi.json"
    path = "/docs"
    pageTitle = "Index API documentation"
}

private val scalarInternalProperties = baseScalarProperties().apply {
    url = "/docs/internal/openapi.json"
    path = "/docs/internal"
    pageTitle = "Index API internal documentation"
}


fun Route.documentationRoutes() {
    //////////////
    /// PUBLIC ///
    //////////////

    get("/docs/openapi.json") {
        val doc = OpenApiDoc(info = openApiInfo) +
                call.application.routingRoot.descendants().filter {
                    !it.attributes.contains(IxRouteAttributeKey.Internal.attributeKey)
                            && it.children.none { child ->
                        child.attributes.contains(IxRouteAttributeKey.Internal.attributeKey)
                                || child.children.any { childOfChild -> childOfChild.attributes.contains(IxRouteAttributeKey.Internal.attributeKey) }
                    }
                }
        call.respondText(text=jsonEncoder.encodeToString(doc), contentType = ContentType.Application.Json)
    }.hide()

    get("/docs") {
        call.respondText(
            text = ScalarHtmlRenderer.render(scalarProperties),
            contentType = ContentType.Text.Html,
            status = HttpStatusCode.OK
        )
    }.hide()

    get("/docs/scalar.js") {
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
        get("/docs/internal/openapi.json") {
            val doc = OpenApiDoc(info = openApiInfo) + call.application.routingRoot.descendants()
            call.respondText(text=jsonEncoder.encodeToString(doc), contentType = ContentType.Application.Json)
        }.hide()

        get("/docs/internal") {
            call.respondText(
                text = ScalarHtmlRenderer.render(scalarInternalProperties),
                contentType = ContentType.Text.Html,
                status = HttpStatusCode.OK
            )
        }.hide()
    }

    get("/docs/internal/scalar.js") {
        call.respondBytes(
            bytes = ScalarHtmlRenderer.getScalarJsContent(),
            contentType = ContentType.Application.JavaScript,
            status = HttpStatusCode.OK
        )
    }.hide()
}