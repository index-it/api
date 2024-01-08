package app.index.api.routing.web.routes

import app.index.api.routing.web.WebhookRoute
import app.index.core.clients.FCMClient
import app.index.data.daos.auth.EmailVerificationDao
import app.index.data.daos.auth.PasswordResetDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.daos.user.FCMRegistrationTokenDao
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
    val emailVerificationDao by inject<EmailVerificationDao>()
    val passwordResetDao by inject<PasswordResetDao>()

    get<WebhookRoute.TaskReminderJobRoute>({
        tags = listOf("web")
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
        // Get the job related data
        val taskReminderJobDto = taskReminderJobDao.get(it.id)
            ?: return@get call.respond(HttpStatusCode.OK)

        // Get all the notification registration tokens associated with that user
        val notificationRegistrationTokens = fcmRegistrationTokenDao.getOfUser(taskReminderJobDto.userId).map { fcmRegistrationTokenDto ->
            fcmRegistrationTokenDto.token
        }

        // Send the task reminder notification to all registration tokens found
        fcmClient.sendTaskReminderNotification(taskReminderJobDto.task.name, notificationRegistrationTokens)

        // First mark the job as completed
        // (we don't want to delete it first from the database because if that errors this request won't succeed, and we'll receive another webhook for retry)
        call.respond(HttpStatusCode.OK)

        // Delete the job as it has been fulfilled
        taskReminderJobDao.delete(taskReminderJobDto.id)
    }

    get<WebhookRoute.DailyJobRoute>({
        tags = listOf("web")
        operationId = "daily-job"
        summary = "receives webhooks for actions that should be executed daily (cleaning up expired db data for example)"
        response {
            HttpStatusCode.OK to {
                description = "handled"
            }
        }
    }) {
        fcmRegistrationTokenDao.deleteExpired()
        emailVerificationDao.deleteExpired()
        passwordResetDao.deleteExpired()

        call.respond(HttpStatusCode.OK)
    }
}
