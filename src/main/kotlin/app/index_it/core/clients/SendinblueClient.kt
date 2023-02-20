package app.index_it.core.clients

import app.index_it.Env
import app.index_it.models.email.SendinblueCodeOperationRequestBody
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
import java.net.URLEncoder

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
                    SendinblueCodeOperationRequestBody.To(
                        email = email
                    )
                ),
                templateId = 1,
                params = SendinblueCodeOperationRequestBody.Params(
                    url = "https://api.index-it.app/verify-email?email=${
                        URLEncoder.encode(
                            email,
                            "utf-8"
                        )
                    }&code=${code}"
                )
            ))
        }

        return response.status.isSuccess()
    }

    suspend fun sendPasswordResetEmail(email: String, code: String): Boolean {
        val response: HttpResponse = client.post("smtp/email") {
            setBody(SendinblueCodeOperationRequestBody(
                to = listOf(
                    SendinblueCodeOperationRequestBody.To(
                        email = email
                    )
                ),
                templateId = 2,
                params = SendinblueCodeOperationRequestBody.Params(
                    url = "https://index-it.app/change-password?code=${code}"
                )
            ))
        }

        return response.status.isSuccess()
    }

    fun close() {
        client.close()
    }
}
