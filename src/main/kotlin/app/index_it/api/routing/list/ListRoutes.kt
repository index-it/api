package app.index_it.api.routing.list

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.list.routes.*
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/lists")
@Suppress("unused")
class ListsRoute {
    @Resource("{listId}")
    class ListRoute(val parent: ListsRoute = ListsRoute(), val listId: String) {
        @Resource("categories")
        class CategoriesRoute(val parent: ListRoute) {
            @Resource("{categoryId}")
            class CategoryRoute(val parent: CategoriesRoute, val categoryId: String)
        }

        @Resource("items")
        class ItemsRoute(val parent: ListRoute, val completed: Boolean? = null) {
            @Resource("{itemId}")
            class ItemRoute(val parent: ItemsRoute, val itemId: String) {
                @Resource("content")
                class ContentRoute(val parent: ItemRoute)

                @Resource("completion")
                class CompletionRoute(val parent: ItemRoute, val completed: Boolean)
            }
        }
    }
}

fun Route.listRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        listsRoute()
        listRoute()

        categoriesRoute()
        categoryRoute()

        itemsRoute()
        itemRoute()
        itemContentRoute()
        itemCompletionRoute()
    }
}
