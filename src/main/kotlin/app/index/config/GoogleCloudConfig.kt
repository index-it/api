package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("google")
object GoogleCloudConfig {
    @ConfigurationProperty("cloud.scheduler.project")
    lateinit var project: String

    @ConfigurationProperty("cloud.scheduler.location")
    lateinit var location: String

    /**
     * This **must be set as an environment variable** in order to work with FCM and Google Cloud Scheduler.
     *
     * For example:
     * `GOOGLE_APPLICATION_CREDENTIALS=./gcp-service-account.json`
     *
     * Reference: https://github.com/googleapis/google-cloud-java#authentication
     */
    @ConfigurationProperty("application.credentials")
    lateinit var applicationCredentialPath: String
}
