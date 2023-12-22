package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("google")
object GoogleCloudConfig {
    @ConfigurationProperty("cloud.scheduler.project")
    lateinit var project: String

    @ConfigurationProperty("cloud.scheduler.location")
    var location: String = "us-east1"

    /**
     * This **must be set as an environment variable** in order to work with FCM and Google Cloud Scheduler in production.
     *
     * For example:
     * `GOOGLE_APPLICATION_CREDENTIALS=./gcp-service-account.json`
     *
     * For local development see https://cloud.google.com/docs/authentication/provide-credentials-adc?hl=it#local-dev
     *
     * Reference: https://github.com/googleapis/google-cloud-java#authentication
     */
    @ConfigurationProperty("application.credentials")
    var applicationCredentialPath: String = "./gcp-service-account.json"
}
