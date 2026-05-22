package app.index.api.routing.user.routes

import app.index.api.core.exceptions.AuthenticationException
import app.index.shared.core.logic.PasswordEncoder
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.shared.core.data.daos.auth.UserSessionDao
import app.index.shared.core.data.daos.user.UserDao
import app.index.shared.core.data.models.auth.PasswordResetRequestBody
import app.index.shared.core.data.models.auth.UserSessionCookie
import app.index.api.plugins.authSessionDataOrThrow
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.user.MeRoute
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
     * Get the logged in user data.
     *
     * Tag: user
     *
     * Security: session
     */
    get<MeRoute> {
        val user = userDao.get(userIdFromSessionOrThrow())
            ?: throw AuthenticationException()

        call.respond(user.getResponseDto())
    }

    /**
     * Changes the password of the user.
     *
     * Tag: user
     *
     * Security: session
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
     * Delete the logged in user account and all their data.
     *
     * Tag: user
     *
     * Security: session
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
