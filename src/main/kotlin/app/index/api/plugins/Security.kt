package app.index.api.plugins

import app.index.api.plugins.custom.apiKey
import app.index.config.ApiConfig
import app.index.config.RevenueCatConfig
import app.index.core.exceptions.AuthenticationException
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.auth.UserSessionCookie
import app.index.data.models.user.UserData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

/**
 * Available authentication methods for api routes
 */
object AuthenticationMethods {
    const val EMAIL_VERIFICATION_FORM_AUTH = "email_verification_form_auth"
    const val USER_SESSION_AUTH = "user_session_auth"
    const val ADMIN_BEARER_AUTH = "admin_bearer_auth"
    const val REVENUECAT_WEBHOOKS = "revenuecat_webhooks"
}

/**
 * Used to store the Id in the email verification routes (that cannot use proper session authentication)
 */
class UserIdPrincipalForEmailVerificationAuth(val id: IxId<UserData>)

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
            cookie.extensions["SameSite"] = "None"

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
                val session = userSessionDao.get(userSessionCookie.user_id, userSessionCookie.session_id)

                // If there is no session or if it has expired
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

        apiKey(AuthenticationMethods.REVENUECAT_WEBHOOKS) {
            headerName = "Authorization"

            validate { apiKey ->
                if (apiKey == RevenueCatConfig.webhookSecret || apiKey == "Bearer ${RevenueCatConfig.webhookSecret}") {
                    UserIdPrincipal("revenuecat")
                } else {
                    null
                }
            }
        }
    }
}

/**
 * Gets the current [UserAuthSessionData]
 */
fun RoutingContext.authSessionData(): UserAuthSessionData? = call.principal<UserAuthSessionData>()

/**
 * Gets the current [UserAuthSessionData]
 *
 * @throws AuthenticationException if not authenticated
 */
fun RoutingContext.authSessionDataOrThrow(): UserAuthSessionData = authSessionData() ?: throw AuthenticationException()


/**
 * Gets the [IxId] of the session authenticated [UserData]
 */
fun RoutingContext.userIdFromSession(): IxId<UserData>? = call.principal<UserAuthSessionData>()?.userId

/**
 * Gets the [IxId] of the session authenticated [UserData]
 *
 * @throws AuthenticationException if not authenticated
 */
fun RoutingContext.userIdFromSessionOrThrow(): IxId<UserData> = userIdFromSession() ?: throw AuthenticationException()
