package app.index_it.api.routing.task.routes

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

fun Route.taskCompletionRoute() {
    put<TasksRoute.TaskRoute.CompletionRoute>({
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
            HttpStatusCode.MethodNotAllowed to {
                description = "cannot un-complete a recurring task"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        val updatedTask = TaskDao.setCompletion(userId, it.parent.taskId, it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (updatedTask.itemId != null) {
            ItemDao.get(userId, updatedTask.itemId)
                ?.also { linkedItem ->
                    ItemDao.setCompletion(userId, linkedItem.listId, linkedItem.id, it.completed)
                }
        }

        if (it.completed) {
            TaskDao.calculateNextOccurrenceDueDateAndRRule(updatedTask)
                ?.also { (dueDate, rrule) ->
                    TaskDao.createNextOccurrence(updatedTask, dueDate, rrule)
                    // TODO: WS
                }
        } else if (updatedTask.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed)
        }

        call.respond(updatedTask)
    }
}