package app.index_it.api.plugins

import app.index_it.Env
import app.index_it.core.extentions.toDtoId
import app.index_it.core.logic.PasswordEncoder
import app.index_it.daos.user.UserDao
import app.index_it.daos.auth.UserSessionDao
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import io.ktor.util.date.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.litote.kmongo.Id
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

/**
 * This is the content of the auth-user-session cookie
 */
@Serializable
@Suppress("PropertyName")
data class UserSessionCookie(
    val session_id: String,
    val user_id: String
) : Principal

/**
 * This is used to store the Id in the email verification routes (that cannot use proper session authentication)
 */
data class UserIdPrincipalForEmailVerificationAuth(val id: Id<UserDto>) : Principal

/**
 * Gets the Id of a UserDto from the auth-user-session UserSessionDto
 */
fun PipelineContext<Unit, ApplicationCall>.userIdFromSession(): Id<UserDto>? = call.principal<UserSessionDto>()?.userId

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<UserSessionCookie>("user_session_id") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = Env.session_max_age_in_seconds
            cookie.secure = Env.cookie_secure
            cookie.httpOnly = true

            serializer = KotlinxSessionSerializer(Json {
                prettyPrint = true
                serializersModule = IdKotlinXSerializationModule
            })
        }
    }

    install(Authentication) {
        // Used only for email verification operation
        form("auth-email-verification") {
            userParamName = "email"
            passwordParamName = "password"
            validate {  credentials ->
                UserDao.getFromEmail(credentials.name)?.let {
                    if (it.password_hash == null)
                        null
                    else if (PasswordEncoder.matches(credentials.password, it.password_hash))
                        UserIdPrincipalForEmailVerificationAuth(it.id)
                    else
                        null
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        session<UserSessionCookie>("auth-user-session") {
            validate { userSessionCookie ->
                val session = UserSessionDao.get(userSessionCookie.user_id.toDtoId(), userSessionCookie.session_id)

                // If there is no session or if it has expired (session expires after 7 days)
                if (session == null || (getTimeMillis() - session.iat) >= (Env.session_max_age_in_seconds*1000))
                    null
                else
                    session
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        bearer("auth-bearer-admin") {
            realm = "Access to the '/admin' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == Env.admin_api_key) {
                    UserIdPrincipal("admin")
                } else {
                    null
                }
            }
        }
    }
}
