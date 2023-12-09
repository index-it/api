package app.index_it.api.routing.task.routes
/*
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.data.daos.list.ItemDao
import app.index_it.data.daos.task.TaskDao
import app.index_it.data.models.tasks.TaskDto
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskConnectionRoute() {
    put<TasksRoute.TaskRoute.ConnectionRoute>({
        tags = listOf("tasks")
        operationId = "task-connection"
        summary = "connects or removes a connection of a task to an item"
        request {
            pathParameter<String>("taskId") {
                required = true
                description = "the id of the task"
            }
            queryParameter<String?>("itemId") {
                required = false
                description = "id of the item or null to remove the connection"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the updated task"
                body<TaskDto>()
            }
            HttpStatusCode.NotFound to {
                description = "task or connected item not found"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "cannot manage connection on recurring task"
            }
        }
    }) {
        // TODO: Move to simple update route?
        val userId = userIdFromSession()!!
        val taskId = it.parent.taskId
        val itemId = it.itemId

        val task = TaskDao.get(userId, taskId)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (task.rrule != null)
            return@put call.respond(HttpStatusCode.MethodNotAllowed)

        // if statement for small performance check
        // checks if an update is actually needed, if the values are equal it doesn't perform the if code
        if (task.itemId != itemId) {
            val originalConnectedItem = task.itemId?.let { originalItemId -> ItemDao.get(userId, originalItemId) }

            // unlinks the old item if existing
            if (originalConnectedItem != null) {
                ItemDao.setTaskConnection(userId, originalConnectedItem.listId, originalConnectedItem.id, null)
            }

            // links the new item if required
            if (itemId != null) {
                val newConnectedItem = ItemDao.get(userId, itemId)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                ItemDao.setTaskConnection(userId, newConnectedItem.listId, newConnectedItem.id, taskId)
                    ?: return@put call.respond(HttpStatusCode.NotFound)
            }
        }

        val updatedTask = TaskDao.setItemConnection(userId, it.parent.taskId, itemId)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(updatedTask)
    }
}
 */