package app.index_it.plugins.routes

import app.index_it.core.clients.SendinblueClient
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.*
import app.index_it.models.Validatable
import app.index_it.models.lists.*
import app.index_it.models.user.ClientUserDto
import app.index_it.models.email.EmailVerificationDto
import app.index_it.models.user.UserDto
import app.index_it.models.user.UserSessionDto
import app.index_it.plugins.UserSessionId
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.toId
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

@Serializable
private open class LoginData(
    open val email: String,
    open val password: String
): Validatable<LoginData> {
    override fun validate(): ValidationResult<LoginData> =
        Validation<LoginData> {
            LoginData::email {
                pattern("\\w+@\\w+\\.\\w+") hint "Please provide a valid email address"
            }
            LoginData::password {
                minLength(8)
                maxLength(100)
                pattern("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])") hint "Password needs at least an uppercase character, a lowercase one and a number"
            }
        }.invoke(this)
}

fun PipelineContext<Unit, ApplicationCall>.userId(): Id<UserDto>? = call.principal<UserSessionDto>()?.userId

fun Route.user() {
    post("/login") {
        val loginData = call.receive<LoginData>()
        val user = UserDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        val hashedPassword = BCryptPasswordEncoder().encode(loginData.password)
        if (user.password_hash !== hashedPassword)
            throw AuthenticationException()

        // User must be verified
        if (!user.email_verified)
            return@post call.respond(HttpStatusCode.MethodNotAllowed)

        val userSessionId = UserSessionId((getTimeMillis().toString() +  generateSessionId()).toId())

        val userSessionDto = UserSessionDto(userSessionId.session_id, getTimeMillis(), user.id)
        UserSessionDao.create(userSessionDto)

        call.sessions.set(userSessionId)
        call.respond(HttpStatusCode.OK)
    }

    /**
     * When a user signs up, he needs to send an email and password,
     * and he will be able to log in into his account only once he has verified the email
     */
    post("/signup") {
        val signupData = call.receive<LoginData>()
        if (UserDao.exists(signupData.email)) {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }

        val hashedPassword = BCryptPasswordEncoder().encode(signupData.password)
        val user = UserDto(
            email = signupData.email,
            password_hash = hashedPassword,
            creation_timestamp = getTimeMillis()
        )

        UserDao.create(user)

        val emailVerificationDto = EmailVerificationDto(
            user_email = user.email,
            expire_at = Date(getTimeMillis() + 3600000) // After 60 minutes the verification code will expire
        )
        EmailVerificationDao.save(emailVerificationDto)
        SendinblueClient.sendEmailVerificationEmail(user.email, emailVerificationDto.code)

        call.respond(HttpStatusCode.OK)
    }

    get("/send-verification-email") {
        val email = call.request.queryParameters["email"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val user = UserDao.getFromEmail(email)
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        if (user.email_verified)
            call.respond(HttpStatusCode.OK)

        // Maximum 3 every 60 minutes
        if (EmailVerificationDao.isRateLimited(email))
            return@get call.respond(HttpStatusCode.TooManyRequests)

        val emailVerificationDto = EmailVerificationDto(
            user_email = email,
            expire_at = Date(getTimeMillis() + 3600000) // After 60 minutes the verification code will expire
        )
        EmailVerificationDao.save(emailVerificationDto)
        val sent = SendinblueClient.sendEmailVerificationEmail(email, emailVerificationDto.code)
        if (sent)
            call.respond(HttpStatusCode.OK)
        else
            call.respond(HttpStatusCode.InternalServerError)
    }

    get("/verify-email") {
        val code = call.request.queryParameters["code"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)
        val email = call.request.queryParameters["email"]?.let { URLDecoder.decode(it, "utf-8") }
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val userDto = UserDao.getFromEmail(email)
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        // Check if user is already verified
        if (userDto.email_verified)
            return@get call.respond(HttpStatusCode.OK)

        val emailVerificationDto = EmailVerificationDao.get(code)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (code === emailVerificationDto.code) {
            EmailVerificationDao.delete(code)
            return@get call.respond(HttpStatusCode.NotFound)
        } else {
            return@get call.respond(HttpStatusCode.NotFound)
        }
    }

    // TODO: Password reset

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
             * Updates a user profile
             */
            put {
                val clientDto = call.receive<ClientUserDto>()
                val user = UserDao.update(userId()!!, clientDto)
                call.respond(user ?: HttpStatusCode.NotFound)
            }

            /**
             * Deletes a user
             */
            delete {
                UserDao.delete(userId()!!)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/lists") {
            /**
             * Gets all lists of the user
             */
            get {
                call.respond(ListDao.getAll(userId()!!))
            }

            /**
             * Creates a new list for the user
             **/
            put {
                val clientDto = call.receive<ClientListDto>()

                ListDao.create(userId()!!, clientDto)
                call.respond(HttpStatusCode.OK)
            }

            route("/{list_id}") {
                /**
                 * Updates a list of the user
                 */
                put {
                    val clientDto = call.receive<ClientListDto>()

                    val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()

                    val list = ListDao.update(userId()!!, listId, clientDto)
                    call.respond(list ?: HttpStatusCode.NotFound)
                }

                /**
                 * Deletes a list of the user
                 */
                delete {
                    val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()

                    ListDao.delete(userId()!!, listId)
                    call.respond(HttpStatusCode.OK)
                }

                route("/category") {
                    /**
                     * Creates a new category for a list
                     */
                    put {
                        val clientDto = call.receive<ClientCategoryDto>()
                        val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()

                        val listDto = ListDao.CategoryDao.create(userId()!!, listId, clientDto)
                        call.respond(listDto ?: HttpStatusCode.NotFound)
                    }

                    route("/{category_id}") {
                        /**
                         * Updates a category for a list
                         */
                        put {
                            val clientDto = call.receive<ClientCategoryDto>()
                            val listId: Id<ListDto> = call.parameters["list:id"]!!.toId()
                            val categoryId: Id<CategoryDto> = call.parameters["category_id"]!!.toId()

                            val listDto = ListDao.CategoryDao.update(userId()!!, listId, categoryId, clientDto)
                            call.respond(listDto ?: HttpStatusCode.NotFound)
                        }

                        delete {
                            val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()
                            val categoryId: Id<CategoryDto> = call.parameters["category_id"]!!.toId()

                            val listDto = ListDao.CategoryDao.delete(userId()!!, listId, categoryId)
                            call.respond(listDto ?: HttpStatusCode.NotFound)
                        }
                    }
                }

                route("/items") {
                    get {
                        val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()

                        val items = ItemDao.getAll(userId()!!, listId)
                        call.respond(items)
                    }

                    put {
                        val clientDto = call.receive<ClientItemDto>()
                        val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()

                        val itemDto = ItemDao.create(userId()!!, listId, clientDto)
                        call.respond(itemDto)
                    }

                    route("/{item_id}") {
                        put {
                            val clientDto = call.receive<ClientItemDto>()
                            val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()
                            val itemId: Id<ItemDto> = call.parameters["item_id"]!!.toId()

                            val itemDto = ItemDao.update(userId()!!, listId, itemId, clientDto)
                            call.respond(itemDto ?: HttpStatusCode.NotFound)
                        }

                        delete {
                            val listId: Id<ListDto> = call.parameters["list_id"]!!.toId()
                            val itemId: Id<ItemDto> = call.parameters["item_id"]!!.toId()

                            ItemDao.delete(userId()!!, listId, itemId)
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                }
            }
        }
    }
}
