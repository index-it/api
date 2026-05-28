package app.index.api.routing.admin.routes

import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.shared.core.data.daos.auth.UserSessionDao
import app.index.shared.core.data.daos.user.UserDao
import app.index.api.plugins.custom.internal
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.admin.AdminRoute
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminUsersByEmailRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * gets a user by its email
     *
     * @tag admin
     * @operationId get-user-by-email
     * @security admin_bearer_auth
     * @query email the encoded email of the user
     * @response 200 user found
     * @response 404 user with the provided email not found
     */
    get<AdminRoute.UsersRoute.UserByEmailRoute> {
        val user = userDao.getFromEmail(it.email)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(user)
    }.internal()

    /**
     * verifies the email on behalf of a user
     *
     * @tag admin
     * @operationId verify-user-email
     * @security admin_bearer_auth
     * @query email the encoded email of the user
     * @response 200 email verified
     * @response 404 user with the provided email not found
     */
    get<AdminRoute.UsersRoute.UserByEmailRoute.VerifyEmailRoute> {
        userDao.getFromEmail(it.parent.email)?.let { user ->
            userDao.verifyEmail(user.id)
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.OK)
    }.internal()

    /**
     * deletes an user by its email
     *
     * @tag admin
     * @operationId delete-user-by-email
     * @security admin_bearer_auth
     * @query email the encoded email of the user
     * @response 200 user deleted
     * @response 404 user with the provided email not found
     */
    delete<AdminRoute.UsersRoute.UserByEmailRoute> {
        val userId = userDao.getFromEmail(it.email)?.id
            ?: return@delete call.respond(HttpStatusCode.NotFound)

        userDao.delete(userId)
        userSessionDao.deleteAllOfUser(userId)

        call.respond(HttpStatusCode.OK)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent,
            users = setOf(userId)
        )
    }.internal()
}
