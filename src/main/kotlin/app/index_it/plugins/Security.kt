package app.index_it.plugins

import app.index_it.Env
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.SessionDao
import app.index_it.daos.UserDao
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

val oauthHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            serializersModule = IdKotlinXSerializationModule
        })
    }
}

fun Application.configureSecurity() {
    /*
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
            urlProvider = { "http://localhost:${Env.ktor_port}/callback" }
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
            client = oauthHttpClient
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
            call.respondRedirect("/user")
        }

        authenticate("auth-oauth-google") {
            get("google-oauth-login") {
                // Redirects to 'authorizeUrl' automatically
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                call.sessions.set(GoogleUserSession(principal?.accessToken.toString()))
                call.respondRedirect("/user")
            }
        }
    }*/
}

@Serializable
data class SessionId(
    val id: String
) : Principal

private data class GoogleUserSession(val token: String)

@Serializable
private data class LoginData(
    val id: String,
    val password: String
)
