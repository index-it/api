package app.index_it.api.routing.list

import app.index_it.api.routing.list.routes.*
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/lists")
class ListsRoute {
    @Resource("{list_id}")
    class ListRoute(val parent: ListsRoute = ListsRoute(), val list_id: String) {
        @Resource("categories")
        class CategoriesRoute(val parent: ListRoute) {
            @Resource("{category_id}")
            class CategoryRoute(val parent: CategoriesRoute, val category_id: String)
        }

        @Resource("items")
        class ItemsRoute(val parent: ListRoute) {
            @Resource("{item_id}")
            class ItemRoute(val parent: ItemsRoute, val item_id: String)
        }
    }
}

fun Route.listRoutes() {
    authenticate("auth-user-session") {
        listsRoute()
        listRoute()

        categoriesRoute()
        categoryRoute()

        itemsRoute()
        itemRoute()
    }
}
