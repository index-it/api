package app.index.api.routing.task.routes

import app.index.shared.core.data.daos.list.CategoryDao
import app.index.shared.core.data.daos.list.ItemDao
import app.index.shared.core.data.daos.list.ListDao
import app.index.shared.core.data.daos.task.TaskDao
import app.index.shared.core.data.models.tasks.ConnectedTaskItemsData
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.connectedTaskItemsRoute() {
    val taskDao by inject<TaskDao>()
    val listDao by inject<ListDao>()
    val categoryDao by inject<CategoryDao>()
    val itemDao by inject<ItemDao>()

    /**
     * Gets all the tasks of a user with an optional completion filter.
     *
     * Tag: tasks
     *
     * Security: session
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