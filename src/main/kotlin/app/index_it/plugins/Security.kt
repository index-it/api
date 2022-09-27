package app.index_it.plugins

import app.index_it.Env
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import app.index_it.models.user.UserSessionDto
import app.index_it.plugins.custom.apiKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

// TODO: Needs serializable?
data class ApiKeyPrincipal(val key: String) : Principal

@Serializable
data class UserSessionId(
    val session_id: String
) : Principal

@Serializable
private data class LoginData(
    val email: String,
    val password: String
)

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<UserSessionId>("user_session_id") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 604800 // 7 days
            cookie.secure = true
            cookie.httpOnly = true
        }
    }

    install(Authentication) {
        // Uses `X-Api-Key` header
        apiKey("auth-full-api-key") {
            validate { apiKey ->
                apiKey
                    .takeIf { it == Env.full_access_api_key }
                    ?.let { ApiKeyPrincipal(it) }
            }
            // Challenge already handled
        }

        session<UserSessionId>("auth-session") {
            validate { userSessionId ->
                val session = UserSessionDao.get(userSessionId.session_id)

                // If there is no session or if it has expired (session expires after 7 days)
                if (session == null || (getTimeMillis() - session.iat) >= 604800000)
                    null
                else
                    session
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    routing {
        post("/login") {
            val userLoginData = call.receive<LoginData>()
            val user = UserDao.getFromEmail(userLoginData.email)
                ?: throw AuthenticationException()

            val encodedPassword = BCryptPasswordEncoder().encode(userLoginData.password)
            if (user.password_hash !== encodedPassword)
                throw AuthenticationException()

            val userSessionId = UserSessionId(getTimeMillis().toString() +  generateSessionId())

            val userSessionDto = UserSessionDto(userSessionId.session_id, getTimeMillis(), user.id)
            UserSessionDao.create(userSessionDto)

            call.sessions.set(userSessionId)
            call.respond(HttpStatusCode.OK)
        }
    }
}
