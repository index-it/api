package app.index_it.core.clients

import app.index_it.config.GoogleCloudSchedulerConfig
import app.index_it.config.WebhookConfig
import app.index_it.core.clients.GoogleCloudSchedulerClient.createTaskReminderJob
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskReminderJobDto
import com.google.cloud.scheduler.v1.*
import com.google.protobuf.Timestamp

/**
 * Client to interact with Google Cloud Scheduler
 *
 * @see [createTaskReminderJob]
 */
object GoogleCloudSchedulerClient {

    // Docs: https://cloud.google.com/java/docs/reference/google-cloud-scheduler/latest/com.google.cloud.scheduler.v1
    private val cloudSchedulerClient = CloudSchedulerClient.create(
        CloudSchedulerSettings.newBuilder()
            .build()
    )

    // TODO: Security - https://cloud.google.com/scheduler/docs/http-target-auth?hl=it#:~:text=To%20authenticate%20between%20Cloud%20Scheduler,using%20HTTPS%2C%20to%20the%20target.

    /**
     * Creates a job for the given [taskReminderJob]
     *
     * The scheduled job will run at [reminderTimestamp] - 1000 milliseconds (so one second before)
     *
     * @param taskReminderJob
     * @param reminderTimestamp
     *
     * @throws Exception failed to create job
     */
    fun createTaskReminderJob(taskReminderJob: TaskReminderJobDto, reminderTimestamp: Long) {
        val jobId = taskReminderJob.id

        val webhookUrl = "${WebhookConfig.taskReminderJob}?id=${jobId}"
        val httpTarget = HttpTarget.newBuilder()
            .setHttpMethod(HttpMethod.POST)
            .setUri(webhookUrl)

        val parent = LocationName.of(GoogleCloudSchedulerConfig.project, GoogleCloudSchedulerConfig.location).toString()
        val seconds = (reminderTimestamp / 1000) - 1
        val job = Job.newBuilder()
            .setName(jobId.toString())
            .setHttpTarget(httpTarget)
            .setScheduleTime(Timestamp.newBuilder().setSeconds(seconds).build())
            .build()

        cloudSchedulerClient.createJob(
            CreateJobRequest.newBuilder()
                .setParent(parent)
                .setJob(job)
                .build()
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
        val name = JobName.of(GoogleCloudSchedulerConfig.project, GoogleCloudSchedulerConfig.location, id.toString())
        cloudSchedulerClient.deleteJob(name)
    }
}