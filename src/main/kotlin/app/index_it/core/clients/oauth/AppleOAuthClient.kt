package app.index_it.core.clients.oauth

import app.index_it.config.OAuthConfig
import app.index_it.data.models.oauth.apple.AppleIdTokenDto
import app.index_it.data.models.oauth.apple.AppleOAuthTokenResponseDto
import app.index_it.di.IClosableComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.koin.core.annotation.Single

private val log = KotlinLogging.logger {  }

@Single(createdAtStart = true)
class AppleOAuthClient(
    private val httpClient: HttpClient
) : IClosableComponent {
    suspend fun exchangeCodeAndGetUserInfo(code: String): AppleIdTokenDto? {
        return try {
            val response = httpClient.submitForm(
                url = "https://appleid.apple.com/auth/token",
                formParameters = Parameters.build {
                    append("client_id", OAuthConfig.appleClientId)
                    append("client_secret", OAuthConfig.appleClientSecret)
                    append("redirect_uri", OAuthConfig.appleRedirectUri)
                    append("grant_type", "authorization_code")
                    append("code", code)
                }
            )

            if (response.status.isSuccess()) {
                response.body<AppleOAuthTokenResponseDto>().let {
                    log.debug { "Exchanged code for id token with Apple OAuth\nData: $it" }
                    it.idToken
                }
            } else {
                log.error { "Failed exchanging apple oauth code for token\nResponse: $response" }
                null
            }
        } catch (e: Exception) {
            log.error(e) { "Failed exchanging apple oauth code for token" }
            null
        }
    }

    override fun close() {
        httpClient.close()
    }
}
