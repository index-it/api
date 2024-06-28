package app.index.api.routing.admin

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.admin.routes.adminUsersByEmailRoute
import app.index.api.routing.admin.routes.adminUsersByIdRoute
import app.index.api.routing.admin.routes.adminUsersRoute
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("/admin")
class AdminRoute {
    @Resource("users")
    class UsersRoute(val parent: AdminRoute = AdminRoute()) {
        @Resource("/id/{user_id}")
        class UserByIdRoute(val parent: UsersRoute = UsersRoute(), @Contextual val user_id: IxId<UserData>)

        @Resource("/email")
        class UserByEmailRoute(val parent: UsersRoute = UsersRoute(), val email: String) {
            @Resource("/verify-email")
            class VerifyEmailRoute(val parent: UserByEmailRoute)
        }
    }
}

fun Route.adminRoutes() {
    authenticate(AuthenticationMethods.ADMIN_BEARER_AUTH) {
        adminUsersRoute()
        adminUsersByIdRoute()
        adminUsersByEmailRoute()
    }
}
