package app.index_it.plugins

import app.index_it.Env
import app.index_it.daos.UserSessionDao
import app.index_it.plugins.custom.apiKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import io.ktor.util.date.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.litote.kmongo.Id
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

@Serializable
data class ApiKeyPrincipal(val key: String) : Principal

@Serializable
data class UserSessionId(
    @Contextual val session_id: Id<UserSessionId>
) : Principal

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<UserSessionId>("user_session_id") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 604800 // 7 days
            cookie.secure = Env.use_secure_cookies
            cookie.httpOnly = true

            serializer = KotlinxSessionSerializer(Json {
                prettyPrint = true
                serializersModule = IdKotlinXSerializationModule
            })
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
}
