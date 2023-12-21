package app.index.core.clients

import app.index.config.GoogleCloudConfig
import app.index.config.JobConfig
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskReminderJobDto
import com.google.cloud.scheduler.v1.*
import com.google.protobuf.Timestamp
import org.koin.core.annotation.Single

/**
 * Client to interact with Google Cloud Scheduler
 *
 * @see [createTaskReminderJob]
 */
@Single(createdAtStart = true)
class GoogleCloudSchedulerClient {
    // Docs: https://cloud.google.com/java/docs/reference/google-cloud-scheduler/latest/com.google.cloud.scheduler.v1
    private val cloudSchedulerClient =
        CloudSchedulerClient.create(
            CloudSchedulerSettings.newBuilder()
                .build(),
        )

    /**
     * Creates the job for sending a webhook once a day
     * to check expiration of Firebase Cloud Messaging registration tokens
     */
    fun createFCMRegistrationTokenExpirationJobIfMissing() {
        val jobExists =
            try {
                cloudSchedulerClient.getJob(JobConfig.fcmRegistrationTokenExpirationJobId)
            } catch (_: Exception) {
                null
            }.let { it != null }

        if (!jobExists) {
            val httpTarget =
                HttpTarget.newBuilder()
                    .setHttpMethod(HttpMethod.GET)
                    .setUri(JobConfig.fcmRegistrationTokenExpirationWebhookUrl)

            val parent = LocationName.of(GoogleCloudConfig.project, GoogleCloudConfig.location).toString()
            val job =
                Job.newBuilder()
                    .setName(JobConfig.fcmRegistrationTokenExpirationJobId)
                    .setHttpTarget(httpTarget)
                    .setSchedule("0 0 * * *")
                    .build()

            cloudSchedulerClient.createJob(
                CreateJobRequest.newBuilder()
                    .setParent(parent)
                    .setJob(job)
                    .build(),
            )
        }
    }

    /**
     * Creates a job for the given [taskReminderJob]
     *
     * The scheduled job will run at [reminderTimestamp] - 1000 milliseconds (so one second before)
     *
     * @param jobId
     * @param reminderTimestamp
     *
     * @throws Exception failed to create job
     */
    fun createTaskReminderJob(
        jobId: IxId<TaskReminderJobDto>,
        reminderTimestamp: Long,
    ) {
        val webhookUrl = "${JobConfig.taskReminderWebhookUrl}/$jobId"
        val httpTarget =
            HttpTarget.newBuilder()
                .setHttpMethod(HttpMethod.GET)
                .setUri(webhookUrl)

        val parent = LocationName.of(GoogleCloudConfig.project, GoogleCloudConfig.location).toString()
        val seconds = (reminderTimestamp / 1000) - 1
        val job =
            Job.newBuilder()
                .setName(jobId.toString())
                .setHttpTarget(httpTarget)
                .setScheduleTime(Timestamp.newBuilder().setSeconds(seconds).build())
                .build()

        cloudSchedulerClient.createJob(
            CreateJobRequest.newBuilder()
                .setParent(parent)
                .setJob(job)
                .build(),
        )
    }

    /**
     * Deletes a job by its id
     *
     * @param id job id
     *
     * @throws Exception
     */
    fun deleteTaskReminderJob(id: IxId<TaskReminderJobDto>) {
        val name = JobName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, id.toString())
        cloudSchedulerClient.deleteJob(name)
    }
}
