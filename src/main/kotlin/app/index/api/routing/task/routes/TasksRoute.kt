package app.index.api.routing.task.routes

import app.index.api.plugins.userIdFromSession
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.clients.GoogleCloudSchedulerClient
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.TaskUseCase
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderJobData
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
                body<List<TaskData>>()
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
                description = "invalid parameters, see error message"
            }
            HttpStatusCode.NotFound to {
                description = "did not find the item provided for connection with this new task"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val newTask = call.receive<TaskData.TaskCreateRequestData>()
        val itemIdToConnect = newTask.itemId

        //////////////////
        /// VALIDATION ///
        //////////////////

        if (itemIdToConnect != null && newTask.rrule != null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot create recurring connected task")
        }

        /////////////////////
        /// TASK CREATION ///
        /////////////////////

        val task = if (itemIdToConnect != null) {
            ////////////////////
            /// CONNECT ITEM ///
            ////////////////////
            val itemToConnect = itemDao.get(userId, itemIdToConnect)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            val task = taskDao.create(userIdFromSession()!!, newTask)

            itemDao.setTaskConnection(userId, itemToConnect.listId, itemToConnect.id, task.id)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            task
        } else {
            taskDao.create(userIdFromSession()!!, newTask)
        }

        /////////////////
        /// REMINDERS ///
        /////////////////
        val taskReminderJobCreateData = TaskUseCase.calculateReminderTimestamps(task.dueDate, task.reminders).map {  timestamp ->
            TaskReminderJobData.TaskReminderJobCreateData(
                id = newIxId(),
                taskId = task.id,
                userId = userId,
                scheduledAt = timestamp
            )
        }

        taskReminderJobDao.createAll(taskReminderJobCreateData)

        taskReminderJobCreateData.forEach {
            googleCloudSchedulerClient.createTaskReminderJob(it.id, it.scheduledAt)
        }

        call.respond(task)
    }
}
