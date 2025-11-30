package app.index.api.routing.task.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.task.TaskDao
import app.index.data.daos.user.UserDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.tasks.TaskData
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
     * gets all the tasks of a user with an optional completion filter
     *
     * @tag tasks
     * @operationId get-tasks
     * @query completed completion filter: true only completed, false only uncompleted, null or missing means all
     * @response 200 the tasks
     * @response 401 user not authenticated
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
     * creates a new task
     *
     * @tag tasks
     * @operationId create-task
     * @requestBody application/json task data
     * @response 200 the task
     * @response 400 invalid parameters
     * @response 401 user not authenticated
     * @response 402 pro required to have multiple reminders
     * @response 404 did not find the item provided for connection with this new task
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

            if (!user.has_pro) {
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
