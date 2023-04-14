package app.index_it.api.routing.web

import app.index_it.core.db.NotifyDBM
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder

@Resource("/notify/{email}")
class NotifyRoute(val email: String)

fun Route.webRoutes() {
    get<NotifyRoute> {
        val email = withContext(Dispatchers.IO) {
            URLDecoder.decode(it.email, "utf-8")
        }

        NotifyDBM.notify(email)

        call.respond(HttpStatusCode.OK)
    }
}
