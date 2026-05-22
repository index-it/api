package app.index.api.core.clients.oauth

import app.index.api.config.OAuthConfig
import app.index.api.data.models.oauth.google.GoogleUserInfoData
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

    private val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(listOf(OAuthConfig.googleClientId))
        .build()

    /**
     * User a google [token] to retrieve [GoogleUserInfoData]
     */
    fun getUserInfoFromIdTokenIfValid(token: String): GoogleUserInfoData? {
        val idToken = verifier.verify(token) ?: return null
        val payload = idToken.payload

        return GoogleUserInfoData(
            email = payload.email,
            verifiedEmail = payload.emailVerified,
        )
    }
}
