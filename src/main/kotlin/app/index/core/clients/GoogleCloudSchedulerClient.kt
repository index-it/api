package app.index.core.clients

import app.index.config.GoogleCloudConfig
import app.index.config.JobConfig
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskReminderJobData
import com.google.cloud.scheduler.v1.*
import com.google.protobuf.Timestamp
import org.koin.core.annotation.Single

/**
 * Client to interact with Google Cloud Scheduler
 *
 * @see createTaskReminderJob
 * @see createDailyJobIfMissing
 */
@Single(createdAtStart = true)
class GoogleCloudSchedulerClient {
    // Docs: https://cloud.google.com/java/docs/reference/google-cloud-scheduler/latest/com.google.cloud.scheduler.v1
    private val cloudSchedulerClient =
        CloudSchedulerClient.create(
            CloudSchedulerSettings.newBuilder()
                .build(),
        )

    init {
        createDailyJobIfMissing()
    }

    /**
     * Creates the daily job that notifies the backend to perform certain actions daily
     */
    private fun createDailyJobIfMissing() {
        val jobName = JobName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, JobConfig.dailyJobId).toString()

        val jobExists =
            try {
                cloudSchedulerClient.getJob(jobName)
            } catch (_: Exception) {
                null
            }.let { it != null }

        if (!jobExists) {
            val httpTarget = HttpTarget.newBuilder()
                .setHttpMethod(HttpMethod.GET)
                .setUri(JobConfig.dailyJobWebhookUrl)

            val parent = LocationName.of(GoogleCloudConfig.project, GoogleCloudConfig.location).toString()
            val job = Job.newBuilder()
                .setName(jobName)
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
        val httpTarget = HttpTarget.newBuilder()
            .setHttpMethod(HttpMethod.GET)
            .setUri(webhookUrl)

        val parent = LocationName.of(GoogleCloudConfig.project, GoogleCloudConfig.location).toString()
        val jobName = JobName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, id.toString()).toString()
        // This won't be precise if too far in the future if timezones change
        val seconds = (reminderTimestamp / 1000) - 1
        val job = Job.newBuilder()
            .setName(jobName)
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
    fun deleteTaskReminderJob(id: IxId<TaskReminderJobData>) {
        val name = JobName.of(GoogleCloudConfig.project, GoogleCloudConfig.location, id.toString())
        cloudSchedulerClient.deleteJob(name)
    }
}
