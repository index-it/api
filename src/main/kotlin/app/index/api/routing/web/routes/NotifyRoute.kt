package app.index.api.routing.web.routes

import app.index.api.routing.web.NotifyRoute
import app.index.data.models.web.NotifyDto
import app.index.data.sources.db.dbi.user.NotifyDBI
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.notifyRoute() {
    val notifyDBI by inject<NotifyDBI>()

    /**
     * Adds the user email to the newsletter for the alpha release.
     *
     * Tag: web
     *
     * Security: bearer
     */
    get<NotifyRoute> {
        notifyDBI.create(NotifyDto(it.email))

        call.respond(HttpStatusCode.OK)
    }
}
