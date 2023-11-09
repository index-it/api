package app.index_it.data.models.oauth.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Docs: https://developers.google.com/identity/protocols/oauth2/web-server#httprest
@Serializable
data class GoogleOAuthTokenResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String?,
    @SerialName("expires_in")
    val expiresInSeconds: Long,
    val scope: String
)
