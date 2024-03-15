package app.index.api.routing.web.routes

import app.index.api.routing.web.NotifyRoute
import app.index.data.models.web.NotifyDto
import app.index.data.sources.db.dbi.user.NotifyDBI
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.notifyRoute() {
    val notifyDBI by inject<NotifyDBI>()

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
        notifyDBI.create(NotifyDto(it.email))

        call.respond(HttpStatusCode.OK)
    }
}
