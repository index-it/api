package app.index_it.core.clients.oauth

import app.index_it.Env
import app.index_it.models.oauth.apple.AppleIdTokenDto
import app.index_it.models.oauth.apple.AppleOAuthTokenResponseDto
import app.index_it.models.oauth.google.GoogleOAuthTokenResponseDto
import app.index_it.models.oauth.google.GoogleUserInfoDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

object AppleOAuthClient {
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

    suspend fun exchangeCodeAndGetUserInfo(code: String): AppleIdTokenDto? {
        return try {
            val response = client.submitForm(
                url = "https://appleid.apple.com/auth/token",
                formParameters = Parameters.build {
                    append("client_id", Env.apple_client_id)
                    append("client_secret", Env.apple_client_secret)
                    append("redirect_uri", Env.apple_redirect_uri)
                    append("grant_type", "authorization_code")
                    append("code", code)
                }
            )

            if (response.status.isSuccess()) {
                response.body<AppleOAuthTokenResponseDto>().idToken
            } else {
                log.error("Failed exchanging apple oauth code for token\nResponse: $response")
                null
            }
        } catch (e: Exception) {
            log.error("Failed exchanging apple oauth code for token", e)
            null
        }
    }

    fun close() {
        client.close()
    }
}
