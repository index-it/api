package app.index_it.api.routing.routes

import app.index_it.core.exceptions.AuthenticationException
import app.index_it.core.extentions.toDtoId
import app.index_it.daos.*
import app.index_it.models.lists.*
import app.index_it.models.user.UserDto
import app.index_it.models.user.UserSessionDto
import app.index_it.api.plugins.UserSessionId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.Id

fun PipelineContext<Unit, ApplicationCall>.userIdFromSession(): Id<UserDto>? = call.principal<UserSessionDto>()?.userId

fun Route.user() {
    /**
     * OAuth's ways to register for a user
     */
    get("/login-with-google") {
        // val code = call.request.queryParameters["code"]
        // Exchange the code for the token

        // Get the email and id with the token

        // If
        // the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        // Update email verification field if set to false
        // Create session

        // Else
        // Extract the sub from the id
        // Create the user in the db with a random id, the email gotten from Google, email verified to true, the google_id gotten
        // Respond with an ok status code (DO NOT REQUIRE EMAIL VERIFICATION)
        // Create session
    }

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
                val user = UserDao.get(userIdFromSession()!!)
                    ?: throw AuthenticationException()

                call.respond(user)
            }

            /**
             * Deletes a user
             */
            delete {
                UserDao.delete(userIdFromSession()!!)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/lists") {
            /**
             * Gets all lists of the user
             */
            get {
                call.respond(ListDao.getAll(userIdFromSession()!!))
            }

            /**
             * Creates a new list for the user
             **/
            put {
                val clientDto = call.receive<ClientListDto>()

                ListDao.create(userIdFromSession()!!, clientDto)
                call.respond(HttpStatusCode.OK)
            }

            route("/{list_id}") {
                /**
                 * Updates a list of the user
                 */
                put {
                    val clientDto = call.receive<ClientListDto>()

                    val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()

                    val list = ListDao.update(userIdFromSession()!!, listId, clientDto)

                    call.respond(list ?: HttpStatusCode.NotFound)
                }

                /**
                 * Deletes a list of the user
                 */
                delete {
                    val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()

                    ListDao.delete(userIdFromSession()!!, listId)
                    call.respond(HttpStatusCode.OK)
                }

                route("/category") {
                    /**
                     * Creates a new category for a list
                     */
                    put {
                        val clientDto = call.receive<ClientCategoryDto>()
                        val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()

                        val listDto = ListDao.CategoryDao.create(userIdFromSession()!!, listId, clientDto)
                        call.respond(listDto ?: HttpStatusCode.NotFound)
                    }

                    route("/{category_id}") {
                        /**
                         * Updates a category for a list
                         */
                        put {
                            val clientDto = call.receive<ClientCategoryDto>()
                            val listId: Id<ListDto> = call.parameters["list:id"]!!.toDtoId()
                            val categoryId: Id<CategoryDto> = call.parameters["category_id"]!!.toDtoId()

                            val listDto = ListDao.CategoryDao.update(userIdFromSession()!!, listId, categoryId, clientDto)
                            call.respond(listDto ?: HttpStatusCode.NotFound)
                        }

                        delete {
                            val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()
                            val categoryId: Id<CategoryDto> = call.parameters["category_id"]!!.toDtoId()

                            val listDto = ListDao.CategoryDao.delete(userIdFromSession()!!, listId, categoryId)
                            call.respond(listDto ?: HttpStatusCode.NotFound)
                        }
                    }
                }

                route("/items") {
                    get {
                        val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()

                        val items = ItemDao.getAll(userIdFromSession()!!, listId)
                        call.respond(items)
                    }

                    put {
                        val clientDto = call.receive<ClientItemDto>()
                        val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()

                        val itemDto = ItemDao.create(userIdFromSession()!!, listId, clientDto)
                        call.respond(itemDto)
                    }

                    route("/{item_id}") {
                        put {
                            val clientDto = call.receive<ClientItemDto>()
                            val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()
                            val itemId: Id<ItemDto> = call.parameters["item_id"]!!.toDtoId()

                            val itemDto = ItemDao.update(userIdFromSession()!!, listId, itemId, clientDto)
                            call.respond(itemDto ?: HttpStatusCode.NotFound)
                        }

                        delete {
                            val listId: Id<ListDto> = call.parameters["list_id"]!!.toDtoId()
                            val itemId: Id<ItemDto> = call.parameters["item_id"]!!.toDtoId()

                            ItemDao.delete(userIdFromSession()!!, listId, itemId)
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                }
            }
        }

        // TODO
        route("/tasks") {}
    }
}
