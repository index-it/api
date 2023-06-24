package app.index_it.api.routing.list

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.list.routes.*
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/lists")
class ListsRoute {
    @Resource("{listId}")
    @Suppress("unused")
    class ListRoute(val parent: ListsRoute = ListsRoute(), val listId: String) {
        @Resource("categories")
        class CategoriesRoute(val parent: ListRoute) {
            @Resource("{categoryId}")
            class CategoryRoute(val parent: CategoriesRoute, val categoryId: String)
        }

        @Resource("items")
        class ItemsRoute(val parent: ListRoute) {
            @Resource("{itemId}")
            class ItemRoute(val parent: ItemsRoute, val itemId: String)
        }
    }

    @Resource("template")
    @Suppress("unused")
    class TemplateRoute(val parent: ListsRoute) {
        @Resource("colors")
        class ColorsRoute(val parent: TemplateRoute)
    }
}

fun Route.listRoutes() {
    authenticate(AuthenticationMethods.userSessionAuth) {
        listsRoute()
        listRoute()

        categoriesRoute()
        categoryRoute()

        itemsRoute()
        itemRoute()

        templateRoute()
    }
}
