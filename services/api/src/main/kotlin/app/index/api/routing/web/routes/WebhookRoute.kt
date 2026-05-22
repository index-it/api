package app.index.api.routing.web.routes

import app.index.api.core.clients.FCMClient
import app.index.api.core.clients.GoogleCloudTasksClient
import app.index.shared.core.logic.DatetimeUtils
import app.index.api.data.daos.auth.EmailVerificationDao
import app.index.api.data.daos.auth.PasswordResetDao
import app.index.api.data.daos.list.ListInviteDao
import app.index.api.data.daos.list.ListUserInviteDao
import app.index.api.data.daos.task.TaskReminderJobDao
import app.index.api.data.daos.user.FCMRegistrationTokenDao
import app.index.api.plugins.custom.internal
import app.index.api.routing.web.WebhookRoute
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.webhookRoute() {
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val fcmRegistrationTokenDao by inject<FCMRegistrationTokenDao>()
    val fcmClient by inject<FCMClient>()
    val emailVerificationDao by inject<EmailVerificationDao>()
    val passwordResetDao by inject<PasswordResetDao>()
    val listUserInviteDao by inject<ListUserInviteDao>()
    val listInviteDao by inject<ListInviteDao>()
    val googleCloudTasksClient by inject<GoogleCloudTasksClient>()

    /**
     * Receives webhooks for task reminder jobs.
     *
     * Tag: web
     */
    get<WebhookRoute.TaskReminderJobRoute> {
        // Get the job related data
        val taskReminderJobDto = taskReminderJobDao.get(it.id)
            ?: return@get call.respond(HttpStatusCode.OK)

        /*
        Google Cloud Tasks allows a max scheduling time of 30 days
        so we always schedule reminders with a max time of 30 days in the future
        here we need to check if the reminder should actually trigger now
        or if we need to reschedule it as we received this event because of the 30 days limitation
        */

        // if the reminder is scheduled for more than 1 minute in the future
        // we create a new job for it, increasing the reschedule count in the db field
        val taskReminderFutureThresholdMillis = 60000
        if (taskReminderJobDto.scheduledAt > (DatetimeUtils.currentMillis() + taskReminderFutureThresholdMillis)) {
            val newJobDto = taskReminderJobDao.increaseRescheduleCount(taskReminderJobDto.id)
            if (newJobDto != null) {
                googleCloudTasksClient.createTaskReminderJob(newJobDto.id, newJobDto.scheduledAt, newJobDto.rescheduleCount)
            }

            return@get call.respond(HttpStatusCode.OK)
        }

        // Get all the notification registration tokens associated with that user
        val notificationRegistrationTokens = fcmRegistrationTokenDao.getOfUser(taskReminderJobDto.userId).map { fcmRegistrationTokenDto ->
            fcmRegistrationTokenDto.token
        }

        // Send the task reminder notification to all registration tokens found
        fcmClient.sendTaskReminderNotification(
            taskId = taskReminderJobDto.task.id,
            taskName = taskReminderJobDto.task.name,
            registrationToken = notificationRegistrationTokens
        )

        // First mark the job as completed
        // (we don't want to delete it first from the database because if that errors this request won't succeed, and we'll receive another webhook for retry)
        call.respond(HttpStatusCode.OK)

        // Delete the job as it has been fulfilled
        taskReminderJobDao.delete(taskReminderJobDto.id)
    }.internal()

    /**
     * Receives webhooks for actions that should be executed daily.
     *
     * Tag: web
     */
    get<WebhookRoute.DailyJobRoute> {
        fcmRegistrationTokenDao.deleteExpired()
        emailVerificationDao.deleteExpired()
        passwordResetDao.deleteExpired()
        listUserInviteDao.deleteExpired()
        listInviteDao.deleteExpired()

        call.respond(HttpStatusCode.OK)
    }.internal()
}
