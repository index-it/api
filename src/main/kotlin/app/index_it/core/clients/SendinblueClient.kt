package app.index_it.core.clients

import app.index_it.Env
import app.index_it.models.email.SendinblueCodeOperationRequestBody
import app.index_it.models.email.SendinblueGenericRequestBody
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.net.URLEncoder

private val log = KotlinLogging.logger { }
object SendinblueClient {
    private val client = HttpClient(Apache) {
        install(Logging)
        install(ContentNegotiation) {
            json(Json)
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        defaultRequest {
            url("https://api.sendinblue.com/v3/")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("api-key", Env.sendinblue_api_key)
        }
    }

    suspend fun sendEmailVerificationEmail(email: String, code: String): Boolean {
        val response: HttpResponse = client.post("smtp/email") {
            setBody(SendinblueCodeOperationRequestBody(
                to = listOf(
                    SendinblueGenericRequestBody.To(
                        email = email
                    )
                ),
                templateId = 2,
                params = SendinblueCodeOperationRequestBody.Params(
                    url = "${Env.email_verification_url}?email=${
                        URLEncoder.encode(
                            email,
                            "utf-8"
                        )
                    }&code=${code}"
                )
            ))
        }

        if (response.status.isSuccess()) {
            log.debug { "Sent email verification to $email" }
        } else {
            log.error("Failed to send email verification code\nResponse: $response")
        }

        return response.status.isSuccess()
    }

    suspend fun sendPasswordResetEmail(email: String, token: String): Boolean {
        val response: HttpResponse = client.post("smtp/email") {
            setBody(SendinblueCodeOperationRequestBody(
                to = listOf(
                    SendinblueGenericRequestBody.To(
                        email = email
                    )
                ),
                templateId = 1,
                params = SendinblueCodeOperationRequestBody.Params(
                    url = "${Env.reset_password_url}?token=${token}"
                )
            ))
        }

        if (response.status.isSuccess()) {
            log.debug { "Sent password reset email to $email" }
        } else {
            log.error("Failed to send password reset email\nResponse: $response")
        }

        return response.status.isSuccess()
    }

    suspend fun sendPasswordResetSuccessEmail(email: String): Boolean {
        val response: HttpResponse = client.post("smtp/email") {
            setBody(SendinblueGenericRequestBody(
                to = listOf(
                    SendinblueGenericRequestBody.To(
                        email = email
                    )
                ),
                templateId = 3
            ))
        }

        if (response.status.isSuccess()) {
            log.debug { "Sent password reset success email to $email" }
        } else {
            log.error("Failed to send password reset success email\nResponse: $response")
        }

        return response.status.isSuccess()
    }

    fun close() {
        client.close()
    }
}
