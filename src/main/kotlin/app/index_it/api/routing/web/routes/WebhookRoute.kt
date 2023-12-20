package app.index_it.api.routing.web.routes

import app.index_it.api.routing.web.WebhookRoute
import app.index_it.core.clients.FCMClient
import app.index_it.data.daos.task.TaskReminderJobDao
import app.index_it.data.daos.user.FCMRegistrationTokenDao
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.webhookRoute() {
    get<WebhookRoute.TaskReminderJobRoute>({
        tags = listOf("web", "webhook")
        operationId = "task-reminder-job-webhook"
        summary = "receives webhooks for task reminder jobs"
        request {
            pathParameter<String>("id") {
                description = "job id"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "handled"
            }
        }
    }) {
        val (jobId, task, userId) = TaskReminderJobDao.get(it.id)
            ?: return@get call.respond(HttpStatusCode.OK)

        TaskReminderJobDao.delete(jobId)

        val notificationRegistrationTokens = FCMRegistrationTokenDao.getOfUser(userId).map { fcmRegistrationTokenDto ->
            fcmRegistrationTokenDto.token
        }

        FCMClient.sendTaskReminderNotification(task.name, notificationRegistrationTokens)

        call.respond(HttpStatusCode.OK)
    }
}