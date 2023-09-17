package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.daos.task.TaskDao
import app.index_it.models.tasks.TaskDto
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskCompletionRoute() {
    get<TasksRoute.TaskRoute.CompletionRoute>({
        tags = listOf("tasks")
        operationId = "task-completion"
        summary = "completes or un-completes a task"
        description = "this completes or un-completes a task and a related item if existing"
        request {
            pathParameter<String>("taskId") {
                required = true
                description = "the id of the task"
            }
            queryParameter<Boolean>("completed") {
                required = true
                description = "true for completed, false for un-completed"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the updated task"
                body<TaskDto>()
            }
            HttpStatusCode.NotFound to {
                description = "task not found"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        val task = TaskDao.setCompletion(userId, it.parent.taskId.toObjectId(), it.completed)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (task.listId != null && task.itemId != null) {
            ItemDao.setCompletion(userId, task.listId, task.itemId, it.completed)
        }

        call.respond(task)
    }
}