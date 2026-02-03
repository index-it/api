package app.index.core.clients

import app.index.config.GoogleCloudConfig
import app.index.config.JobConfig
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskReminderJobData
import com.google.cloud.tasks.v2beta3.*
import com.google.protobuf.Timestamp
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {  }

/**
 * Client to interact with Google Cloud Tasks
 *
 * @see createTaskReminderJob
 */
@Single(createdAtStart = true)
class GoogleCloudTasksClient {
    companion object {
        private const val SCHEDULE_MAX_MILLIS = 2505600000L  // 29 days
    }


    private val cloudTasksClient =
        CloudTasksClient.create(
            CloudTasksSettings.newBuilder()
                .build(),
        )

    init {
        createTaskReminderJobQueueIfMissing()
    }

    private fun createTaskReminderJobQueueIfMissing() {
        val queueName = QueueName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName).toString()

        val queueExists =
            try {
                cloudTasksClient.getQueue(queueName)
            } catch (_: Exception) {
                null
            }.let { it != null }

        if (!queueExists) {
            val createQueueRequest = CreateQueueRequest.newBuilder()
                .setParent(LocationName.of(GoogleCloudConfig.project, GoogleCloudConfig.location).toString())
                .setQueue(
                    Queue.newBuilder()
                        .setName(queueName)
                )
                .build()

            cloudTasksClient.createQueue(createQueueRequest)

            logger.debug { "Create google cloud task reminder job queue with name ${JobConfig.taskReminderJobQueueName}" }
        }
    }

    /**
     * Creates a job to schedule a task reminder webhook
     *
     * The scheduled job will run at [reminderTimestamp] - 1000 milliseconds (so one second before)
     *
     * @param id
     * @param reminderTimestamp
     * @param rescheduleCount the number of times the job has been rescheduled
     *
     * @throws Exception failed to create job
     */
    fun createTaskReminderJob(
        id: IxId<TaskReminderJobData>,
        reminderTimestamp: Long,
        rescheduleCount: Long
    ) {
        val webhookUrl = "${JobConfig.taskReminderJobWebhookUrl}/$id"
        val httpRequest = HttpRequest.newBuilder()
            .setHttpMethod(HttpMethod.GET)
            .setUrl(webhookUrl)
            .putHeaders("Content-Type", "application/json")
            .setOidcToken(
                // this requires 'iam.serviceAccounts.actAs' permission
                // even if the email is the same as the one used for authentication to gcp
                OidcToken.newBuilder()
                    .setServiceAccountEmail(GoogleCloudConfig.authTokenEmail)
                    .setAudience(GoogleCloudConfig.authTokenAudience)
            )

        val parent = QueueName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName).toString()
        val jobName = TaskName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName, "$id-$rescheduleCount").toString()

        // We schedule at maximum for 30 days from now, this is a Google Cloud Tasks limitation
        val maxTimestamp = DatetimeUtils.currentMillis() + SCHEDULE_MAX_MILLIS
        val seconds = ((reminderTimestamp).coerceAtMost(maxTimestamp) / 1000) - 1
        val task = Task.newBuilder()
            .setName(jobName)
            .setHttpRequest(httpRequest)
            .setScheduleTime(Timestamp.newBuilder().setSeconds(seconds).build())
            .build()

        val createdTask = cloudTasksClient.createTask(
            CreateTaskRequest.newBuilder()
                .setParent(parent)
                .setTask(task)
                .build()
        )

        logger.debug { "Created task reminder job with name ${createdTask.name}" }
    }

    /**
     * Deletes a job by its id
     *
     * @param id job id
     * @param rescheduleCount the number of times the job has been rescheduled
     *
     * @throws Exception
     */
    fun deleteTaskReminderJob(id: IxId<TaskReminderJobData>, rescheduleCount: Long) {
        val name = TaskName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName, "$id-$rescheduleCount")
        cloudTasksClient.deleteTask(name)

        logger.debug { "Deleted task reminder job with name $name" }
    }
}