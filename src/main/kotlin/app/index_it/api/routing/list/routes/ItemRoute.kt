package app.index_it.api.routing.list.routes

import app.index_it.api.routing.list.ListsRoute
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.routing.*

fun Route.itemRoute() {
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {

    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {

    }

    delete<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {

    }
}
