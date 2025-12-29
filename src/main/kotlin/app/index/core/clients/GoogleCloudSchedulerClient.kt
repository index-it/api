package app.index.core.clients

import app.index.config.GoogleCloudConfig
import app.index.config.JobConfig
import com.google.cloud.scheduler.v1.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {  }

/**
 * Client to interact with Google Cloud Scheduler
 *
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
                // this requires 'iam.serviceAccounts.actAs' permission
                // even if the email is the same as the one used for authentication to gcp
                .setOidcToken(OidcToken.newBuilder()
                    .setServiceAccountEmail(GoogleCloudConfig.authTokenEmail)
                    .setAudience(GoogleCloudConfig.authTokenAudience)
                )
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

            logger.debug { "Created missing daily job with name ${job.name}" }
        }
    }
}
