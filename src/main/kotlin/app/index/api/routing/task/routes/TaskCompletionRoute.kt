package app.index.api.routing.task.routes

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
    val googleCloudSchedulerClient by inject<GoogleCloudSchedulerClient>()

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

        val updatedTask = taskDao.setCompletion(userId, it.parent.taskId, it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (updatedTask.itemId != null) {
            itemDao.get(userId, updatedTask.itemId)
                ?.also { linkedItem ->
                    itemDao.setCompletion(userId, linkedItem.listId, linkedItem.id, it.completed)
                }
        }

        if (it.completed) {
            taskReminderJobDao.deleteAllOfTask(updatedTask.id)

            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(updatedTask)
                ?.also { (dueDate, rrule) ->
                    val nextOccurrenceTask = taskDao.createNextOccurrence(updatedTask, dueDate, rrule)

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
        } else if (updatedTask.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed)
        }

        call.respond(updatedTask)
    }
}
