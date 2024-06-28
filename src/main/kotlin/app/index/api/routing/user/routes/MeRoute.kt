package app.index.api.routing.user.routes

import app.index.api.plugins.authSessionDataOrThrow
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.user.MeRoute
import app.index.core.exceptions.AuthenticationException
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.PasswordResetRequestBody
import app.index.data.models.auth.UserSessionCookie
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.meRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val passwordEncoder by inject<PasswordEncoder>()

    get<MeRoute>({
        tags = listOf("user")
        operationId = "me"
        summary = "get the logged in user data"
        response {
            HttpStatusCode.OK to {
                description = "user data"
            }
        }
    }) {
        val user = userDao.get(userIdFromSessionOrThrow())
            ?: throw AuthenticationException()

        call.respond(user.getResponseDto())
    }

    put<MeRoute.ChangePasswordRoute>({
        tags = listOf("user")
        operationId = "change-password"
        summary = "changes the password of the user"
        request {
            body<PasswordResetRequestBody> {
                description = "contains the new password"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "password changed"
            }
            HttpStatusCode.BadRequest to {
                description = "password doesn't conform to rules, see response message"
            }
            HttpStatusCode.NotFound to {
                description = "something went wrong"
            }
        }
    }) {
        val authSession = authSessionDataOrThrow()
        val userId = authSession.userId
        val newPassword = call.receive<PasswordResetRequestBody>().password
        val newPasswordHashed = passwordEncoder.encode(newPassword)

        // If the user email wasn't verified before, now it can be considered verified
        userDao.changePassword(
            id = userId,
            newPasswordHashed = newPasswordHashed
        )

        // Invalidate all other user websocket connections
        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent
        )

        // Invalidate all other user active sessions
        userSessionDao.deleteAllOfUserExcept(userId, authSession)

        call.respond(HttpStatusCode.OK)
    }

    delete<MeRoute>({
        tags = listOf("user")
        operationId = "delete-account"
        summary = "delete the logged in user account"
        description = "this deletes **all** the data of the logged in user from index systems, it's irreversible"
        response {
            HttpStatusCode.OK to {
                description = "user data deleted and session terminated"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        call.sessions.clear<UserSessionCookie>()

        userDao.delete(userId)
        userSessionDao.deleteAllOfUser(userId)

        call.respond(HttpStatusCode.OK)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent
        )
    }
}
