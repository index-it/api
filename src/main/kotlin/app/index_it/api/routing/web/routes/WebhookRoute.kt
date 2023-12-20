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
import org.koin.ktor.ext.inject

fun Route.webhookRoute() {
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val fcmRegistrationTokenDao by inject<FCMRegistrationTokenDao>()
    val fcmClient by inject<FCMClient>()

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
        val (jobId, task, userId) = taskReminderJobDao.get(it.id)
            ?: return@get call.respond(HttpStatusCode.OK)

        taskReminderJobDao.delete(jobId)

        val notificationRegistrationTokens = fcmRegistrationTokenDao.getOfUser(userId).map { fcmRegistrationTokenDto ->
            fcmRegistrationTokenDto.token
        }

        fcmClient.sendTaskReminderNotification(task.name, notificationRegistrationTokens)

        call.respond(HttpStatusCode.OK)
    }

    get<WebhookRoute.FCMRegistrationTokenExpirationJobRoute>({
        tags = listOf("web", "webhook")
        operationId = "fcm-registration-token-expiration-job-webhook"
        summary = "receives webhooks for fcm registration token expiration job"
        response {
            HttpStatusCode.OK to {
                description = "handled"
            }
        }
    }) {
        fcmRegistrationTokenDao.deleteExpired()

        call.respond(HttpStatusCode.OK)
    }
}