package app.index.api.routing.task.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.clients.GoogleCloudSchedulerClient
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.TaskUseCase
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.models.tasks.SubTaskData
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderJobData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.taskRoute() {
    val taskDao by inject<TaskDao>()
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val itemDao by inject<ItemDao>()
    val googleCloudSchedulerClient by inject<GoogleCloudSchedulerClient>()

    get<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "get-task"
        summary = "gets a single task"
        request {
            pathParameter<String>("taskId") {
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
        val task =
            taskDao.get(userIdFromSessionOrThrow(), it.taskId)
                ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }

    put<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "update-task"
        summary = "updates a task"
        request {
            pathParameter<String>("taskId") {
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
                        dueDate = 1703500710000,
                        subTasks =
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
        val taskId = it.taskId
        val newItemIdToConnect = updateData.itemId

        val task = taskDao.get(userId, it.taskId)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (task.itemId != null && updateData.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed)
        }

        if (task.itemId != newItemIdToConnect) {
            val originalConnectedItem = task.itemId?.let { originalItemId -> itemDao.get(userId, originalItemId) }

            // un-connects the old item if existing
            if (originalConnectedItem != null) {
                itemDao.setTaskConnection(userId, originalConnectedItem.listId, originalConnectedItem.id, null)
            }

            // connects the new item if required
            if (newItemIdToConnect != null) {
                val newConnectedItem = itemDao.get(userId, newItemIdToConnect)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                itemDao.setTaskConnection(userId, newConnectedItem.listId, newConnectedItem.id, taskId)
                    ?: return@put call.respond(HttpStatusCode.NotFound)
            }
        }

        val updatedTask = taskDao.update(userId, taskId, updateData)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        ///////////////////////////////
        /// ON DAY REMINDER REFRESH ///
        ///////////////////////////////

        val onDayReminderTimestamp = TaskUseCase.calculateOnDayReminderTimestamp(task.dueDate, task.onDayReminder)

        if (onDayReminderTimestamp != null) {
            var jobId = taskReminderJobDao.getAllOfTask(taskId)?.id

            if (jobId != null) {
                googleCloudSchedulerClient.deleteTaskReminderJob(jobId)
            } else {
                jobId = newIxId()
                taskReminderJobDao.create(jobId, taskId, userId)
            }

            googleCloudSchedulerClient.createTaskReminderJob(jobId, onDayReminderTimestamp)
        }

        call.respond(updatedTask)
    }

    delete<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "delete-task"
        summary = "deletes a task"
        request {
            pathParameter<String>("taskId") {
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

        taskReminderJobDao.deleteAllOfTask(it.taskId)

        if (!it.all) {
            taskDao.get(userId, it.taskId)
                ?.also { task ->
                    TaskUseCase.calculateNextOccurrenceDueDateAndRRule(task)
                        ?.also { (dueDate, rrule) ->
                            val nextOccurrenceTask = taskDao.createNextOccurrence(task, dueDate, rrule)

                            // TODO: Extract to some function or side effect in dao?
                            val onDayReminderTimestamp =
                                TaskUseCase.calculateOnDayReminderTimestamp(
                                    nextOccurrenceTask.dueDate,
                                    nextOccurrenceTask.onDayReminder,
                                )

                            if (onDayReminderTimestamp != null) {
                                val jobId = newIxId<TaskReminderJobData>()

                                taskReminderJobDao.create(
                                    jobId = jobId,
                                    taskId = nextOccurrenceTask.id,
                                    userId = userId,
                                )

                                googleCloudSchedulerClient.createTaskReminderJob(jobId, onDayReminderTimestamp)
                            }
                            // TODO: WS
                        }
                }
        }

        taskDao.delete(userId, it.taskId)

        call.respond(HttpStatusCode.OK)
    }
}
