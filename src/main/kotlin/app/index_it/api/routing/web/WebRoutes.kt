package app.index_it.api.routing.web

import app.index_it.core.db.NotifyDBM
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Resource("/notify/{email}")
class NotifyRoute(val email: String)

fun Route.webRoutes() {
    get<NotifyRoute>({
        tags = listOf("web")
        operationId = "subscribe-user-to-newsletter"
        summary = "adds the user email to the newsletter for the alpha release"
        request {
            pathParameter<String>("email") {
                description = "email of the user"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "subscribed"
            }
        }
    }) {
        NotifyDBM.notify(it.email)

        call.respond(HttpStatusCode.OK)
    }
}
