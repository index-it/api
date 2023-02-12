package app.index_it.core.clients

import app.index_it.Env
import app.index_it.models.auth.GoogleOAuthTokenResponseDto
import app.index_it.models.auth.GoogleUserInfoDto
import app.index_it.models.email.From
import app.index_it.models.email.Params
import app.index_it.models.email.SendinblueEmailVerificationRequestBody
import app.index_it.models.email.To
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.net.URLEncoder

private val log = KotlinLogging.logger {  }

object GoogleOAuthClient {
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
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    suspend fun exchangeCodeForToken(code: String): String? {
        return try {
            val googleOAuthTokenResponseDto: GoogleOAuthTokenResponseDto = client.submitForm(
                url = "https://oauth2.googleapis.com/token",
                formParameters = Parameters.build {
                    append("client_id", Env.google_client_id)
                    append("client_secret", Env.google_client_secret)
                    // See https://security.stackexchange.com/questions/44214/what-is-the-purpose-of-oauth-2-0-redirect-uri-checking for why this is needed
                    append("redirect_uri", Env.google_redirect_uri)
                    append("code", code)
                    append("grant_type", "authorization_code")
                }
            ).body()

            googleOAuthTokenResponseDto.accessToken
        } catch (e: Exception) {
            log.error("Failed exchanging google oauth code for token", e)
            null
        }
    }

    suspend fun getUserEmail(token: String): String? {
        return try {
            val googleUserInfoDto: GoogleUserInfoDto = client.get("https://openidconnect.googleapis.com/v1/userinfo") {
                header("Authorization", "Bearer $token")
            }.body()

            googleUserInfoDto.email
        } catch (e: Exception) {
            log.error("Failed fetching google user email with token", e)
            null
        }
    }

    fun close() {
        client.close()
    }
}
