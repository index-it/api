package app.index.api.routing.task.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.pro.ProFeature
import app.index.core.logic.pro.ProManager
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.task.TaskDao
import app.index.data.daos.user.UserDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.tasks.TaskData
import app.index.data.validation.Validations
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.tasksRoute() {
    val taskDao by inject<TaskDao>()
    val userDao by inject<UserDao>()
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    get<TasksRoute>({
        tags = listOf("tasks")
        operationId = "get-tasks"
        summary = "gets all the tasks of a user"
        description = "gets all the tasks of a user with an optional completion filter"
        request {
            queryParameter<Boolean?>("completed") {
                required = false
                description = "completion filter: true only completed, false only uncompleted, null or missing means all"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the tasks"
                body<List<TaskData>>()
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        val tasks = when (it.completed) {
            true -> taskDao.getAllCompleted(userId)
            false -> taskDao.getAllUncompleted(userId)
            null -> taskDao.getAll(userId)
        }

        call.respond(tasks)
    }

    post<TasksRoute>({
        tags = listOf("tasks")
        operationId = "create-task"
        summary = "creates a new task"
        request {
            body<TaskData.TaskCreateRequestData> {
                description = "task data"
                required = true
                example(
                    "sample-task",
                    TaskData.TaskCreateRequestData("find skis", "find some skis for this winter", null, null, emptyList(), emptyList()),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskData>()
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters\n${Validations.Task.VALIDATIONS_SUMMARY}"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.PaymentRequired to {
                description = "pro required to have multiple reminders"
            }
            HttpStatusCode.NotFound to {
                description = "did not find the item provided for connection with this new task"
            }
        }
    }) {
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

            if (!proManager.hasAccessToProFeature(user.stripe_price_id, ProFeature.MULTIPLE_REMINDERS)) {
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
