package app.index_it.plugins.routes

import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.ItemDao
import app.index_it.daos.ListDao
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import app.index_it.models.lists.*
import app.index_it.models.user.ClientUserDto
import app.index_it.models.user.UserDto
import app.index_it.models.user.UserSessionDto
import app.index_it.plugins.UserSessionId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.Id
import org.litote.kmongo.toId

fun PipelineContext<Unit, ApplicationCall>.userId(): Id<UserDto>? = call.principal<UserSessionDto>()?.userId

fun Route.user() {
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

        // TODO
        route("/tasks") {}
    }
}
