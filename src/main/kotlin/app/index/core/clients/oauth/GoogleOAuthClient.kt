package app.index.core.clients.oauth

import app.index.config.OAuthConfig
import app.index.data.models.oauth.google.GoogleUserInfoDto
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class GoogleOAuthClient {
    private val transport: HttpTransport = ApacheHttpTransport()
    private val jsonFactory: JsonFactory = GsonFactory()

    private val verifier =
        GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(OAuthConfig.googleClientId))
            .build()

    fun getUserInfoFromIdTokenIfValid(token: String): GoogleUserInfoDto? {
        val idToken = verifier.verify(token) ?: return null
        val payload = idToken.payload

        return GoogleUserInfoDto(
            email = payload.email,
            verifiedEmail = payload.emailVerified,
        )
    }
}
