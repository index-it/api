package app.index_it.api.routing

import app.index_it.api.routing.auth.auth
import app.index_it.api.routing.routes.admin
import app.index_it.api.routing.routes.user
import app.index_it.core.db.NotifyDBM
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
        // TODO: Remove
        get("/notify/{email}") {
            val email = withContext(Dispatchers.IO) {
                URLDecoder.decode(call.parameters["email"]!!, "UTF-8")
            }
            NotifyDBM.notify(email)
            call.respond(HttpStatusCode.OK)
        }

        auth()

        user()
        admin()
    }
}
