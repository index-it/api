package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.data.daos.list.ItemDao
import app.index_it.data.daos.task.TaskDao
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.taskLinkingRoute() {
    put<TasksRoute.TaskRoute.LinkingRoute>({
        tags = listOf("tasks")
        operationId = "task-linking"
        summary = "links or un-links a task to an item"
        request {
            pathParameter<String>("taskId") {
                required = true
                description = "the id of the task"
            }
            queryParameter<String?>("listId") {
                required = false
                description = "id of the list or null for un-linking"
            }
            queryParameter<String?>("categoryId") {
                required = false
                description = "id of the category or null for un-linking"
            }
            queryParameter<String?>("itemId") {
                required = false
                description = "id of the item or null for un-linking"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the updated task"
                body<TaskDto>()
            }
            HttpStatusCode.NotFound to {
                description = "task or linked item not found"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        val taskId = it.parent.taskId.toObjectId<TaskDto>()
        val listId = it.listId?.toObjectId<ListDto>()
        val categoryId = it.categoryId?.toObjectId<CategoryDto>()
        val itemId = it.itemId?.toObjectId<ItemDto>()

        val originalTask = TaskDao.get(userId, taskId)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        // if statement for small performance check
        // checks if an update is actually needed, if the values are equal it doesn't perform the if code
        if (originalTask.listId != listId || originalTask.itemId != itemId) {
            // unlinks the old item if existing
            if (originalTask.listId != null && originalTask.itemId != null) {
                ItemDao.setLinking(userId, originalTask.listId, originalTask.itemId, null)
            }

            // links the new item if required
            if (listId != null && itemId != null) {
                ItemDao.setLinking(userId, listId, itemId, taskId)
                    ?: return@put call.respond(HttpStatusCode.NotFound)
            }
        }

        val task = TaskDao.setLinking(userId, it.parent.taskId.toObjectId(), listId, categoryId, itemId)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }
}