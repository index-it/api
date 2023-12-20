package app.index_it.core.clients.oauth

import app.index_it.data.models.oauth.facebook.FacebookUserInfoDto
import app.index_it.di.IClosableComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.annotation.Single

private val log = KotlinLogging.logger {  }

@Single(createdAtStart = true)
class FacebookOAuthClient(
    private val httpClient: HttpClient
) : IClosableComponent {

    /*
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
     */

    suspend fun getUserInfo(token: String): FacebookUserInfoDto? {
        return try {
            val response = httpClient.get("https://graph.facebook.com/me") {
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
                log.warn { "Failed fetching facebook user email with token\nResponse: $response" }
                null
            }
        } catch (e: Exception) {
            log.error(e) { "Failed fetching facebook user email with token" }
            null
        }
    }

    override fun close() {
        httpClient.close()
    }
}
