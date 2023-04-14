package app.index_it.core.clients.oauth

import app.index_it.Env
import app.index_it.models.oauth.facebook.FacebookOAuthTokenResponseDto
import app.index_it.models.oauth.facebook.FacebookUserInfoDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

object FacebookOAuthClient {
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
            val response = client.get("https://graph.facebook.com/v16.0/oauth/access_token") {
                url {
                    parameters.append("client_id", Env.facebook_client_id)
                    parameters.append("client_secret", Env.facebook_client_secret)
                    parameters.append("redirect_uri", Env.facebook_redirect_uri)
                    parameters.append("code", code)
                }
            }

            if (response.status.isSuccess()) {
                response.body<FacebookOAuthTokenResponseDto>().let {
                    log.debug { "Exchanged code for Facebook OAuth token\nData: $it" }
                    it.accessToken
                }
            } else {
                log.error("Failed exchanging facebook oauth code for token\nResponse: $response")
                null
            }
        } catch (e: Exception) {
            log.error("Failed exchanging facebook oauth code for token", e)
            null
        }
    }

    suspend fun getUserInfo(token: String): FacebookUserInfoDto? {
        return try {
            val response = client.get("https://graph.facebook.com/me") {
                url {
                    parameters.append("access_token", token)
                    parameters.append("fields", "email")
                }
            }

            if (response.status.isSuccess())
                response.body<FacebookUserInfoDto>().also {
                    log.debug { "Fetched Facebook user info\nData: $it" }
                }
            else {
                log.error("Failed fetching facebook user email with token\nResponse: $response")
                null
            }
        } catch (e: Exception) {
            log.error("Failed fetching facebook user email with token", e)
            null
        }
    }

    fun close() {
        client.close()
    }
}
