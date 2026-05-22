package app.index.api.routing.task.routes

import app.index.api.config.ProConfig
import app.index.api.core.logic.AnalyticsEventManager
import app.index.api.core.logic.usecases.TaskUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.task.TaskDao
import app.index.api.data.daos.user.UserDao
import app.index.api.data.models.analytics.AnalyticsEventData
import app.index.api.data.models.tasks.TaskData
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.tasksRoute() {
    val taskDao by inject<TaskDao>()
    val userDao by inject<UserDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * Gets all the tasks of a user with an optional completion filter.
     *
     * Tag: tasks
     *
     * Security: session
     */
    get<TasksRoute> {
        val userId = userIdFromSessionOrThrow()

        val tasks = when (it.completed) {
            true -> taskDao.getAllCompleted(userId)
            false -> taskDao.getAllUncompleted(userId)
            null -> taskDao.getAll(userId)
        }

        call.respond(tasks)
    }

    /**
     * Creates a new task.
     *
     * Tag: tasks
     *
     * Security: session
     */
    post<TasksRoute> {
        val userId = userIdFromSessionOrThrow()
        val newTask = call.receive<TaskData.TaskCreateRequestData>()
        val itemIdToConnect = newTask.item_id

        //////////////////
        /// VALIDATION ///
        //////////////////

        if (itemIdToConnect != null && newTask.rrule != null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot create recurring connected task")
        }

        if (newTask.reminders.size > 1) {
            val user = userDao.get(userId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            if (!user.has_pro && !ProConfig.bypass) {
                return@post call.respond(HttpStatusCode.PaymentRequired)
            }
        }

        /////////////////////
        /// TASK CREATION ///
        /////////////////////

        val task = taskDao.create(userId, newTask)

        /////////////////
        /// REMINDERS ///
        /////////////////
        TaskUseCase.createReminders(task)

        call.respond(task)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASK_CREATED,
            content = WebsocketEventContent.TaskCreateOrUpdateEventContent(task)
        )

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.TaskCreationEventData(
                user_id = userId,
                item_id = task.item_id,
                sub_tasks_count = task.subtasks.size,
                reminders_count = task.reminders.size,
                is_recurring = task.rrule != null,
                priority = task.priority
            )
        )
    }
}
