package app.index_it.api.routing.list

import app.index_it.api.routing.list.routes.*
import app.index_it.models.lists.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Resource("/lists")
class ListsRoute {
    @Resource("{list-id}")
    class ListRoute(val parent: ListsRoute = ListsRoute(), val list_id: String) {
        @Resource("categories")
        class CategoriesRoute(val parent: ListRoute) {
            @Resource("{category_id}")
            class CategoryRoute(val parent: CategoriesRoute, val category_id: String)
        }

        @Resource("items")
        class ItemsRoute(val parent: ListRoute) {
            @Resource("{item_id}")
            class ItemRoute(val parent: ItemsRoute, val item_id: String)
        }
    }
}

fun Route.list() {
    authenticate("auth-user-session") {
        listsRoute()
        listRoute()

        categoriesRoute()
        categoryRoute()

        itemsRoute()
        itemRoute()

        /*
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
         */
    }
}
