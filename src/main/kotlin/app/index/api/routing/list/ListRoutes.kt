package app.index.api.routing.list

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.list.routes.*
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("/lists")
@Suppress("unused")
class ListsRoute {
    @Resource("{list_id}")
    class ListRoute(
        val parent: ListsRoute = ListsRoute(),
        @Contextual val list_id: IxId<ListData>,
    ) {
        @Resource("categories")
        class CategoriesRoute(val parent: ListRoute) {
            @Resource("{category_id}")
            class CategoryRoute(
                val parent: CategoriesRoute,
                @Contextual val category_id: IxId<CategoryData>,
            )
        }

        @Resource("items")
        class ItemsRoute(val parent: ListRoute, val completed: Boolean? = null) {
            @Resource("{item_id}")
            class ItemRoute(
                val parent: ItemsRoute,
                @Contextual val item_id: IxId<ItemData>,
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
