package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("google")
object GoogleCloudConfig {
    @ConfigurationProperty("cloud.project")
    lateinit var project: String

    @ConfigurationProperty("cloud.location")
    var location: String = "us-east1"

    /**
     * When creating jobs or tasks using Google Cloud Tasks / Scheduler
     * we configure authentication using OIDC tokens.
     * Se when we receive an http request from a task or a job,
     * we validate the token received by checking the email and audience correspond.
     */
    @ConfigurationProperty("auth.token.email")
    lateinit var authTokenEmail: String
    @ConfigurationProperty("auth.token.audience")
    lateinit var authTokenAudience: String


    /**
     * This **must be set as an environment variable** in order to work with FCM and Google Cloud services in production.
     *
     * For example:
     * `GOOGLE_APPLICATION_CREDENTIALS=./gcp-service-account.json`
     *
     * For local development see https://cloud.google.com/docs/authentication/provide-credentials-adc?hl=it#local-dev
     * tldr: run `gcloud auth application-default login`
     *
     * Reference: https://github.com/googleapis/google-cloud-java#authentication
     */
    @ConfigurationProperty("application.credentials")
    @Suppress("UNUSED")
    var applicationCredentialPath: String = "./gcp-service-account.json"
}
