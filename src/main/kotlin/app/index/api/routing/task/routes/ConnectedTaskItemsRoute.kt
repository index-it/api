package app.index.api.routing.task.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.data.daos.list.CategoryDao
import app.index.data.daos.list.ItemDao
import app.index.data.daos.list.ListDao
import app.index.data.daos.task.TaskDao
import app.index.data.daos.user.UserDao
import app.index.data.models.tasks.ConnectedTaskItemsData
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.connectedTaskItemsRoute() {
    val taskDao by inject<TaskDao>()
    val listDao by inject<ListDao>()
    val categoryDao by inject<CategoryDao>()
    val itemDao by inject<ItemDao>()

    /**
     * gets all the tasks of a user with an optional completion filter
     *
     * @tag tasks
     * @operationId get-tasks
     * @query completed completion filter: true only completed, false only uncompleted, null or missing means all
     * @response 200 the tasks
     * @response 401 user not authenticated
     */
    get<TasksRoute.ConnectedItemsRoute> {
        val userId = userIdFromSessionOrThrow()

        val tasks = when (it.parent.completed) {
            true -> taskDao.getAllCompleted(userId)
            false -> taskDao.getAllUncompleted(userId)
            null -> taskDao.getAll(userId)
        }

        val itemsIds = tasks.mapNotNull { task -> task.item_id }
        val items = itemDao.get(itemsIds)

        val categoryIds = items.mapNotNull { item -> item.category_id }
        val listIds = items.map { item -> item.list_id }
        val categories = categoryDao.get(categoryIds)
        val lists = listDao.get(listIds)

        call.respond(
            ConnectedTaskItemsData(
                items = items,
                categories = categories,
                lists = lists
            )
        )
    }
}