package app.index.api.routing.task.routes

import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.config.ProConfig
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.task.TaskDao
import app.index.data.daos.user.UserDao
import app.index.data.models.tasks.TaskData
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.taskRoute() {
    val taskDao by inject<TaskDao>()
    val userDao by inject<UserDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * Gets a single task.
     *
     * Tag: tasks
     *
     * Security: session
     */
    get<TasksRoute.TaskRoute> {
        val task = taskDao.get(userIdFromSessionOrThrow(), it.task_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }

    /**
     * Updates a task.
     *
     * Tag: tasks
     *
     * Security: session
     */
    put<TasksRoute.TaskRoute> {
        val updateData = call.receive<TaskData.TaskUpdateRequestData>()
        val userId = userIdFromSessionOrThrow()
        val taskId = it.task_id

        if (updateData.reminders.size > 1) {
            val user = userDao.get(userId)
                ?: return@put call.respond(HttpStatusCode.Unauthorized)

            if (!user.has_pro && !ProConfig.bypass) {
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

    /**
     * Deletes a task.
     *
     * Tag: tasks
     *
     * Security: session
     */
    delete<TasksRoute.TaskRoute> {
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
