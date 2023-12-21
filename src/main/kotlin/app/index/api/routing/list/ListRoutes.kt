package app.index.api.routing.list

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.list.routes.*
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryDto
import app.index.data.models.lists.ItemDto
import app.index.data.models.lists.ListDto
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("/lists")
@Suppress("unused")
class ListsRoute {
    @Resource("{listId}")
    class ListRoute(
        val parent: ListsRoute = ListsRoute(),
        @Contextual val listId: IxId<ListDto>,
    ) {
        @Resource("categories")
        class CategoriesRoute(val parent: ListRoute) {
            @Resource("{categoryId}")
            class CategoryRoute(
                val parent: CategoriesRoute,
                @Contextual val categoryId: IxId<CategoryDto>,
            )
        }

        @Resource("items")
        class ItemsRoute(val parent: ListRoute, val completed: Boolean? = null) {
            @Resource("{itemId}")
            class ItemRoute(
                val parent: ItemsRoute,
                @Contextual val itemId: IxId<ItemDto>,
            ) {
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
