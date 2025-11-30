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
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.meRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val passwordEncoder by inject<PasswordEncoder>()

    /**
     * get the logged in user data
     *
     * @tag user
     * @operationId me
     * @response 200 user found
     * @response 401 user not authenticated
     */
    get<MeRoute> {
        val user = userDao.get(userIdFromSessionOrThrow())
            ?: throw AuthenticationException()

        call.respond(user.getResponseDto())
    }

    /**
     * changes the password of the user
     *
     * @tag user
     * @operationId change-password
     * @requestBody application/json contains the new password
     * @response 200 password changed
     * @response 400 password doesn't conform to rules, see response message
     * @response 401 user not authenticated
     * @response 404 something went wrong
     */
    put<MeRoute.ChangePasswordRoute> {
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

    /**
     * delete the logged in user account and all their data (irreversible)
     *
     * @tag user
     * @operationId delete-account
     * @response 200 user data deleted and session terminated
     * @response 401 user not authenticated
     */
    delete<MeRoute> {
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
