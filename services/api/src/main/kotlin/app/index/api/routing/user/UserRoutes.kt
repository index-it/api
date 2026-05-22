package app.index.api.routing.user

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.UserData
import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.user.routes.fcmRoutes
import app.index.api.routing.user.routes.logoutRoutes
import app.index.api.routing.user.routes.meRoutes
import app.index.api.routing.user.routes.passwordOperationRoutes
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("/logout")
class LogoutRoute

@Resource("/password-forgotten")
class PasswordForgottenRoute(val email: String)

@Resource("/reset-password")
class ResetPasswordRoute(val token: String)

@Resource("/users")
class UsersRoute {
    @Resource("{user_id}")
    class UserRoute(@Contextual val user_id: IxId<UserData>)
}

@Resource("/me")
class MeRoute {
    @Resource("password")
    class ChangePasswordRoute(val parent: MeRoute)

    @Resource("notifications")
    class NotificationsRoute(val parent: MeRoute) {
        @Resource("registration")
        class RegistrationRoute(val parent: NotificationsRoute)
    }
}

fun Route.userRoutes() {
    passwordOperationRoutes()

    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        logoutRoutes()
        meRoutes()
        fcmRoutes()
    }
}
