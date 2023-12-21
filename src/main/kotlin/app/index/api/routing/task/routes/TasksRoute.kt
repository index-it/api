package app.index.api.routing.task.routes

import app.index.api.plugins.userIdFromSession
import app.index.api.routing.task.TasksRoute
import app.index.core.clients.GoogleCloudSchedulerClient
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.TaskUseCase
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.models.tasks.TaskDto
import app.index.data.models.tasks.TaskReminderJobDto
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
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val itemDao by inject<ItemDao>()
    val googleCloudSchedulerClient by inject<GoogleCloudSchedulerClient>()

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
                body<List<TaskDto>>()
            }
        }
    }) {
        val tasks =
            when (it.completed) {
                true -> taskDao.getAllCompleted(userIdFromSession()!!)
                false -> taskDao.getAllUncompleted(userIdFromSession()!!)
                null -> taskDao.getAll(userIdFromSession()!!)
            }

        call.respond(tasks)
    }

    post<TasksRoute>({
        tags = listOf("tasks")
        operationId = "create-task"
        summary = "creates a new task"
        request {
            body<TaskDto.TaskCreateRequestDto> {
                description = "task data"
                required = true
                example(
                    "sample-task",
                    TaskDto.TaskCreateRequestDto("find skis", "find some skis for this winter", null, null, null, emptyList()),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskDto>()
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters, see error message"
            }
            HttpStatusCode.NotFound to {
                description = "did not find the item provided for connection with this new task"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        val newTask = call.receive<TaskDto.TaskCreateRequestDto>()
        val itemIdToConnect = newTask.itemId

        // ////////////////
        // / VALIDATION ///
        // ////////////////
        // TODO: Move to validate library
        if (itemIdToConnect != null && newTask.rrule != null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot create recurring connected task")
        }

        if (newTask.onDayReminder != null && newTask.dueDate == null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot add on day reminder for task without due date")
        }

        // ///////////////////
        // / TASK CREATION ///
        // ///////////////////

        val task =
            if (itemIdToConnect != null) {
                // //////////////////
                // / CONNECT ITEM ///
                // //////////////////
                val itemToConnect =
                    itemDao.get(userId, itemIdToConnect)
                        ?: return@post call.respond(HttpStatusCode.NotFound)

                val task = taskDao.create(userIdFromSession()!!, newTask)

                itemDao.setTaskConnection(userId, itemToConnect.listId, itemToConnect.id, task.id)
                    ?: return@post call.respond(HttpStatusCode.NotFound)

                task
            } else {
                taskDao.create(userIdFromSession()!!, newTask)
            }

        // /////////////////////
        // / ON DAY REMINDER ///
        // /////////////////////

        val onDayReminderTimestamp = TaskUseCase.calculateOnDayReminderTimestamp(newTask.dueDate, newTask.onDayReminder)

        if (onDayReminderTimestamp != null) {
            val jobId = newIxId<TaskReminderJobDto>()

            taskReminderJobDao.create(
                jobId = jobId,
                taskId = task.id,
                userId = userId,
            )

            googleCloudSchedulerClient.createTaskReminderJob(jobId, onDayReminderTimestamp)
        }

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }
}
