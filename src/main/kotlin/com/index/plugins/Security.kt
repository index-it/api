package com.index.plugins

import io.ktor.server.sessions.*
import io.ktor.server.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import com.index.daos.UserDao
import com.index.core.exceptions.AuthenticationException
import com.index.daos.SessionDao
import com.index.models.user.UserLoginDto
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun Application.configureSecurity() {
    install(Authentication) {

        session<SessionId>("auth-session") {
            validate { sessionId ->
                val session = SessionDao.get(sessionId.id)

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

        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = HttpClient(Apache)
        }
    }

    install(Sessions) {
        cookie<SessionId>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 604800 // 7 days
            cookie.secure = true
            cookie.httpOnly = true
        }
    }

    routing {
        post("/login") {
            val userLoginData = call.receive<LoginData>()
            val userLogin = UserDao.getUser(userLoginData.id)
                ?: throw AuthenticationException()
            // @TODO: Resolve user id and email conflict
            val encodedPassword = BCryptPasswordEncoder().encode(userLoginData.password)
            if (userLogin.password_hash !== encodedPassword)
                throw AuthenticationException()

            call.sessions.set(SessionId(getTimeMillis().toString() +  generateSessionId()))
            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class SessionId(
    val id: String
) : Principal

@Serializable
private data class LoginData(
    val id: String,
    val password: String
)
