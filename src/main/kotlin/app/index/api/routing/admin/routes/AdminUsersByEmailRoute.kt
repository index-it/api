package app.index.api.routing.admin.routes

import app.index.api.plugins.AuthenticationMethods
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

fun Route.adminUsersByEmailRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<AdminRoute.UsersRoute.UserByEmailRoute>({
        tags = listOf("admin")
        operationId = "get-user-by-email"
        summary = "gets a user by its email"
        securitySchemeName = AuthenticationMethods.ADMIN_BEARER_AUTH
        request {
            queryParameter<String>("email") {
                description = "the encoded email of the user"
                example = "sample%40mail.com"
                required = true
                allowEmptyValue = false
                allowReserved = false
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
                description = "user with the provided email not found"
            }
        }
    }) {
        val user = userDao.getFromEmail(it.email)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(user)
    }

    get<AdminRoute.UsersRoute.UserByEmailRoute.VerifyEmailRoute>({
        tags = listOf("admin")
        operationId = "verify-user-email"
        summary = "verifies the email on behalf of a user"
        securitySchemeName = AuthenticationMethods.ADMIN_BEARER_AUTH
        request {
            queryParameter<String>("email") {
                description = "the encoded email of the user"
                example = "sample%40mail.com"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "email verified"
            }
            HttpStatusCode.NotFound to {
                description = "user with the provided email not found"
            }
        }
    }) {
        userDao.getFromEmail(it.parent.email)?.let { user ->
            userDao.verifyEmail(user.id)
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.OK)
    }

    delete<AdminRoute.UsersRoute.UserByEmailRoute>({
        tags = listOf("admin")
        operationId = "delete-user-by-email"
        summary = "deletes an user by its email"
        securitySchemeName = AuthenticationMethods.ADMIN_BEARER_AUTH
        request {
            queryParameter<String>("email") {
                description = "the encoded email of the user"
                example = "sample%40mail.com"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user deleted"
            }
            HttpStatusCode.NotFound to {
                description = "user with the provided email not found"
            }
        }
    }) {
        val userId = userDao.getFromEmail(it.email)?.id
            ?: return@delete call.respond(HttpStatusCode.NotFound)

        userDao.delete(userId)
        userSessionDao.deleteAllOfUser(userId)

        call.respond(HttpStatusCode.OK)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent,
            users = listOf(userId)
        )
    }
}
