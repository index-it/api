package app.index_it.api.routing.user

import app.index_it.api.plugins.UserSessionId
import app.index_it.api.routing.routes.userId
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

@Resource("/logout")
class LogoutRoute()

@Resource("/password-forgotten")
class PasswordForgottenRoute()

@Resource("/request-password-change")
class LogoutRoute()

fun Route.userRoutes() {
    authenticate("auth-session") {
        get("/logout") {
            UserSessionDao.delete(call.sessions.get<UserSessionId>()!!.session_id)
            call.sessions.clear<UserSessionId>()
            call.respond(HttpStatusCode.OK)
        }

        route("/user") {
            /**
             * Gets a single user
             */
            get {
                val user = UserDao.get(userId()!!)
                    ?: throw AuthenticationException()

                call.respond(user)
            }

            /**
             * Deletes a user
             */
            delete {
                UserDao.delete(userId()!!)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
