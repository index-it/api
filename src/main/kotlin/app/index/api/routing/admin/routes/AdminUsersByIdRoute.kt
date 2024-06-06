package app.index.api.routing.admin.routes

import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.admin.AdminRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.user.UserData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminUsersByIdRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<AdminRoute.UsersRoute.UserByIdRoute>({
        tags = listOf("admin")
        operationId = "get-user-by-id"
        summary = "gets a user by its id"
        securitySchemeName = AuthenticationMethods.ADMIN_BEARER_AUTH
        request {
            queryParameter<String>("user_id") {
                description = "the id of the user"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user found"
                body<UserData> {
                    description = "the user data"
                }
            }
            HttpStatusCode.NotFound to {
                description = "user with the provided id not found"
            }
        }
    }) {
        val user = userDao.get(it.user_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(user)
    }

    delete<AdminRoute.UsersRoute.UserByIdRoute>({
        tags = listOf("admin")
        operationId = "delete-user-by-id"
        summary = "deletes an user by its id"
        securitySchemeName = AuthenticationMethods.ADMIN_BEARER_AUTH
        request {
            queryParameter<String>("user_id") {
                description = "the id of the user"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user deleted"
            }
        }
    }) {
        userDao.delete(it.user_id)
        userSessionDao.deleteAllOfUser(it.user_id)

        call.respond(HttpStatusCode.OK)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent,
            users = listOf(it.user_id)
        )
    }
}
