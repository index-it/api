package app.index_it.plugins

import app.index_it.core.db.NotifyDBM
import app.index_it.plugins.routes.admin
import app.index_it.plugins.routes.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder

fun Application.configureRouting() {
    routing {
        get("/notify/{email}") {
            val email = withContext(Dispatchers.IO) {
                URLDecoder.decode(call.parameters["email"]!!, "UTF-8")
            }
            NotifyDBM.notify(email)
            call.respond(HttpStatusCode.OK)
        }

        user()
        admin()
    }
}
