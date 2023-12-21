package app.index.api.plugins

import app.index.config.ApiConfig
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.UserAuthSessionDto
import app.index.data.models.auth.UserSessionCookie
import app.index.data.models.user.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

/**
 * Available authentication methods for api routes
 */
object AuthenticationMethods {
    const val EMAIL_VERIFICATION_FORM_AUTH = "email_verification_form_auth"
    const val USER_SESSION_AUTH = "user_session_auth"
    const val ADMIN_BEARER_AUTH = "admin_bearer_auth"
}

/**
 * Used to store the Id in the email verification routes (that cannot use proper session authentication)
 */
data class UserIdPrincipalForEmailVerificationAuth(val id: IxId<UserDto>) : Principal

/**
 * Gets the Id of a UserDto from the auth-user-session UserSessionDto
 */
fun PipelineContext<Unit, ApplicationCall>.userIdFromSession(): IxId<UserDto>? = call.principal<UserAuthSessionDto>()?.userId

fun Application.configureSecurity() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordEncoder by inject<PasswordEncoder>()

    install(Sessions) {
        cookie<UserSessionCookie>("user_session_id") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = ApiConfig.sessionMaxAgeInSeconds
            cookie.secure = ApiConfig.cookieSecure
            cookie.httpOnly = true

            serializer =
                KotlinxSessionSerializer(
                    Json {
                        serializersModule = IdKotlinXSerializationModule
                    },
                )
        }
    }

    install(Authentication) {
        // Used only for email verification operation
        form(AuthenticationMethods.EMAIL_VERIFICATION_FORM_AUTH) {
            userParamName = "email"
            passwordParamName = "password"
            validate { credentials ->
                userDao.getFromEmail(credentials.name)?.let {
                    if (it.passwordHash == null) {
                        null
                    } else if (passwordEncoder.matches(credentials.password, it.passwordHash)) {
                        UserIdPrincipalForEmailVerificationAuth(it.id)
                    } else {
                        null
                    }
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        session<UserSessionCookie>(AuthenticationMethods.USER_SESSION_AUTH) {
            validate { userSessionCookie ->
                val session = userSessionDao.get(userSessionCookie.userId, userSessionCookie.sessionId)

                // If there is no session or if it has expired (session expires after 7 days)
                if (session == null || (DatetimeUtils.currentMillis() - session.iat) >= (ApiConfig.sessionMaxAgeInSeconds * 1000)) {
                    null
                } else {
                    session
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        bearer(AuthenticationMethods.ADMIN_BEARER_AUTH) {
            authenticate { tokenCredential ->
                if (tokenCredential.token == ApiConfig.adminKey) {
                    UserIdPrincipal("admin")
                } else {
                    null
                }
            }
        }
    }
}
