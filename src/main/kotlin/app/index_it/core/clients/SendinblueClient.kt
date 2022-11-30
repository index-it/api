package app.index_it.core.clients

import app.index_it.Env
import app.index_it.models.email.From
import app.index_it.models.email.Params
import app.index_it.models.email.SendinblueEmailVerificationRequestBody
import app.index_it.models.email.To
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.net.URLEncoder

object SendinblueClient {
    private val client = HttpClient(Jetty) {
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                serializersModule = IdKotlinXSerializationModule
                prettyPrint = true
            })
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
            setBody(SendinblueEmailVerificationRequestBody(
                sender = From(
                    name = "Index Email Verification",
                    email = "verification@index-it.app"
                ),
                to = listOf(
                    To(
                        email = email
                    )
                ),
                templateId = 1,
                params = Params(
                    url = "https://api.index-it.app/verify-email?email=${URLEncoder.encode(email, "utf-8")}&code=${code}"
                )
            ))
        }

        return response.status.isSuccess()
    }
}
