package app.index_it.api.routing.list.routes

import app.index_it.api.routing.list.ListsRoute
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.routing.*

fun Route.itemsRoute() {
    get<ListsRoute.ListRoute.ItemsRoute> {

    }

    post<ListsRoute.ListRoute.ItemsRoute> {

    }
}
