package app.index.api.routing.task.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.models.tasks.TaskData
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.taskCompletionRoute() {
    val taskDao by inject<TaskDao>()
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val itemDao by inject<ItemDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    put<TasksRoute.TaskRoute.CompletionRoute>({
        tags = listOf("tasks")
        operationId = "task-completion"
        summary = "completes or un-completes a task"
        description = "this completes or un-completes a task and a related item if existing"
        request {
            pathParameter<String>("task_id") {
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
                body<TaskData>()
            }
            HttpStatusCode.NotFound to {
                description = "task not found"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "cannot un-complete a recurring task"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        val updatedTask = taskDao.setCompletion(userId, it.parent.task_id, it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (updatedTask.item_id != null) {
            itemDao.setCompletion(userId, updatedTask.item_id, it.completed)
                ?.also { updatedConnectedItem ->
                    emitWebsocketEvent(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.ITEM_UPDATED,
                        content = WebsocketEventContent.ItemCreateOrUpdateEventContent(updatedConnectedItem),
                        includeCurrentSession = true
                    )
                }
        }

        if (it.completed) {
            taskReminderJobDao.deleteAllOfTask(updatedTask.id)

            TaskUseCase.createNextOccurrence(updatedTask)
                ?.also { nextOccurrenceTask ->
                    emitWebsocketEvent(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.TASK_CREATED,
                        content = WebsocketEventContent.TaskCreateOrUpdateEventContent(nextOccurrenceTask),
                        includeCurrentSession = true
                    )
                }
        } else if (updatedTask.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed, "Cannot un-complete task with recurring rule")
        }

        call.respond(updatedTask)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASK_UPDATED,
            content = WebsocketEventContent.TaskCreateOrUpdateEventContent(updatedTask)
        )
    }
}
