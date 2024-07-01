package app.index.api.routing.task.routes

import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.pro.ProFeature
import app.index.core.logic.pro.ProManager
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.task.TaskDao
import app.index.data.daos.user.UserDao
import app.index.data.models.tasks.SubTaskData
import app.index.data.models.tasks.TaskData
import app.index.data.validation.Validations
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject

fun Route.taskRoute() {
    val taskDao by inject<TaskDao>()
    val userDao by inject<UserDao>()
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "get-task"
        summary = "gets a single task"
        request {
            pathParameter<String>("task_id") {
                required = true
                description = "the id of the task"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskData>()
            }
            HttpStatusCode.NotFound to {
                description = "task not found"
            }
        }
    }) {
        val task = taskDao.get(userIdFromSessionOrThrow(), it.task_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }

    put<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "update-task"
        summary = "updates a task"
        request {
            pathParameter<String>("task_id") {
                required = true
                description = "the id of the task"
            }
            body<TaskData.TaskUpdateRequestData> {
                description = "new task data"
                required = true
                example(
                    name = "sample-task-update",
                    value =
                    TaskData.TaskUpdateRequestData(
                        name = "ski equipment",
                        description = "find ski equipment for winter",
                        due_date = LocalDate(2023, 12, 2),
                        subtasks =
                        mutableListOf(
                            SubTaskData(
                                "skis",
                                true,
                            ),
                            SubTaskData(
                                "helmet",
                                false,
                            ),
                            SubTaskData(
                                "goggles",
                                false,
                            ),
                        ),
                    ),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the updated task"
                body<TaskData>()
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters\n${Validations.Task.VALIDATIONS_SUMMARY}"
            }
            HttpStatusCode.PaymentRequired to {
                description = "pro required to have multiple reminders"
            }
            HttpStatusCode.NotFound to {
                description = "task or item to connect not found"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "cannot set an rrule for a task connected to an item (cannot make task recurrent if connected to an item)"
            }
        }
    }) {
        val updateData = call.receive<TaskData.TaskUpdateRequestData>()
        val userId = userIdFromSessionOrThrow()
        val taskId = it.task_id

        if (updateData.reminders.size > 1) {
            val user = userDao.get(userId)
                ?: return@put call.respond(HttpStatusCode.Unauthorized)

            if (!proManager.hasAccessToProFeature(user.stripe_price_id, ProFeature.MULTIPLE_REMINDERS)) {
                return@put call.respond(HttpStatusCode.PaymentRequired)
            }
        }

        val task = taskDao.get(userId, it.task_id)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (task.item_id != null && updateData.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed)
        }

        val updatedTask = taskDao.update(userId, taskId, updateData)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        TaskUseCase.refreshReminders(updatedTask)

        call.respond(updatedTask)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASK_UPDATED,
            content = WebsocketEventContent.TaskCreateOrUpdateEventContent(updatedTask)
        )
    }

    delete<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "delete-task"
        summary = "deletes a task"
        request {
            pathParameter<String>("task_id") {
                required = true
                description = "the id of the task"
            }
            queryParameter<Boolean>("all") {
                required = false
                description = "only useful when a task is recurrent: true for deleting all occurrences, false to keep recurrent tasks"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "task deleted"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val task = taskDao.get(userId, it.task_id)

        if (!it.all && task !== null) {
            TaskUseCase.createNextOccurrence(task)
                ?.also { nextOccurrenceTask ->
                    emitWebsocketEventForCurrentSessionUser(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.TASK_CREATED,
                        content = WebsocketEventContent.TaskCreateOrUpdateEventContent(nextOccurrenceTask),
                        includeCurrentSession = true
                    )
                }
        }

        val deleted = taskDao.delete(userId, it.task_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEventForCurrentSessionUser(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.TASK_DELETED,
                content = WebsocketEventContent.TaskDeleteEventContent(it.task_id)
            )
        }
    }
}
