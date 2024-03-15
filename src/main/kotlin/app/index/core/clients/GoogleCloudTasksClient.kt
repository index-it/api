package app.index.core.clients

import app.index.config.GoogleCloudConfig
import app.index.config.JobConfig
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
     *
     * @throws Exception failed to create job
     */
    fun createTaskReminderJob(
        id: IxId<TaskReminderJobData>,
        reminderTimestamp: Long,
    ) {
        val webhookUrl = "${JobConfig.taskReminderJobWebhookUrl}/$id"
        val httpRequest = HttpRequest.newBuilder()
            .setHttpMethod(HttpMethod.GET)
            .setUrl(webhookUrl)
            .putHeaders("Content-Type", "application/json")

        val parent = QueueName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName).toString()
        val jobName = TaskName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName, id.toString()).toString()
        // This won't be precise if too far in the future if timezones change
        val seconds = (reminderTimestamp / 1000) - 1
        val job = Task.newBuilder()
            .setName(jobName)
            .setHttpRequest(httpRequest)
            .setScheduleTime(Timestamp.newBuilder().setSeconds(seconds).build())
            .build()

        cloudTasksClient.createTask(
            CreateTaskRequest.newBuilder()
                .setParent(parent)
                .setTask(job)
                .build()
        )

        logger.debug { "Created task reminder job with name ${job.name}" }
    }

    /**
     * Deletes a job by its id
     *
     * @param id job id
     *
     * @throws Exception
     */
    fun deleteTaskReminderJob(id: IxId<TaskReminderJobData>) {
        val name = TaskName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.taskReminderJobQueueName, id.toString())
        cloudTasksClient.deleteTask(name)

        logger.debug { "Deleted task reminder job with name $name" }
    }
}