package app.index.api.routing.list

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.list.routes.*
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.lists.ListInviteData
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
        @Resource("access")
        class AccessRoute(val parent: ListRoute) {
            @Resource("invites")
            class InvitesRoute(val parent: AccessRoute) {
                @Resource("{invite_id}")
                class InviteRoute(
                    val parent: InvitesRoute,
                    @Contextual val invite_id: IxId<ListInviteData>,
                )
            }

            @Resource("users")
            class UsersRoute(val parent: AccessRoute)

            @Resource("leave")
            class LeaveRoute(val parent: AccessRoute)
        }

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
            @Resource("completion")
            class CompletionRoute(val parent: ItemsRoute, val completed: Boolean)

            @Resource("move")
            class MoveRoute(val parent: ItemsRoute)

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

    @Resource("accept-invitation")
    class AcceptInvitation(val parent: ListsRoute = ListsRoute(), val token: String)
}

fun Route.listRoutes() {
    listAcceptInvitationRoute()

    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        listsRoute()
        listRoute()
        listAccessInvitesRoute()
        listAccessUsersRoute()

        categoriesRoute()
        categoryRoute()

        itemsRoute()
        itemRoute()
        itemContentRoute()
        itemCompletionRoute()
    }
}
