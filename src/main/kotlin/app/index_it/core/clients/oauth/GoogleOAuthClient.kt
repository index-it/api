package app.index_it.core.clients.oauth

import app.index_it.Env
import app.index_it.models.oauth.google.GoogleUserInfoDto
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory

object GoogleOAuthClient {
    private val transport: HttpTransport = ApacheHttpTransport()
    private val jsonFactory: JsonFactory = GsonFactory()

    private val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(listOf(Env.google_client_id))
        .build()

    fun getUserInfoFromIdTokenIfValid(token: String): GoogleUserInfoDto? {
        val idToken = verifier.verify(token) ?: return null
        val payload = idToken.payload

        return GoogleUserInfoDto(
            email = payload.email,
            verifiedEmail = payload.emailVerified
        )
    }
}

/*
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
            val response = client.submitForm(
                url = "https://oauth2.googleapis.com/token",
                formParameters = Parameters.build {
                    append("client_id", Env.google_client_id)
                    append("client_secret", Env.google_client_secret)
                    // See https://security.stackexchange.com/questions/44214/what-is-the-purpose-of-oauth-2-0-redirect-uri-checking
                    // for why this is needed
                    append("redirect_uri", Env.google_redirect_uri)
                    append("code", code)
                    append("grant_type", "authorization_code")
                }
            )

            if (response.status.isSuccess()) {
                response.body<GoogleOAuthTokenResponseDto>().let {
                    log.debug { "Exchanged code for Google OAuth token\nData: $it" }
                    it.accessToken
                }
            } else {
                log.error("Failed exchanging google oauth code for token\nResponse: $response")
                null
            }
        } catch (e: Exception) {
            log.error("Failed exchanging google oauth code for token", e)
            null
        }
    }

    suspend fun getUserInfo(token: String): GoogleUserInfoDto? {
        return try {
            val response = client.get("https://openidconnect.googleapis.com/v1/userinfo") {
                header("Authorization", "Bearer $token")
            }

            if (response.status.isSuccess())
                response.body<GoogleUserInfoDto>().also {
                    log.debug { "Fetched Google user info\nData: $it" }
                }
            else {
                log.error("Failed fetching google user email with token\nResponse: $response")
                null
            }
        } catch (e: Exception) {
            log.error("Failed fetching google user email with token", e)
            null
        }
    }

    fun close() {
        client.close()
    }
}
*/
