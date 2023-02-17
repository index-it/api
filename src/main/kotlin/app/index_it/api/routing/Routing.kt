package app.index_it.api.routing

import app.index_it.api.routing.auth.auth
import app.index_it.core.db.NotifyDBM
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.net.URLDecoder

fun Application.configureRouting() {
    // Needed for typed queries
    install(Resources) {
        serializersModule = IdKotlinXSerializationModule
    }

    routing {
        /**
         * Unsure if this is good for the documentation or not
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }
        **/

        // TODO: Remove
        get("/notify/{email}") {
            val email = withContext(Dispatchers.IO) {
                URLDecoder.decode(call.parameters["email"]!!, "UTF-8")
            }
            NotifyDBM.notify(email)
            call.respond(HttpStatusCode.OK)
        }

        auth()

        // user()
    }
}
