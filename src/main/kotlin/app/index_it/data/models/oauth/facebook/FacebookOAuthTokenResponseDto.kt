package app.index_it.data.models.oauth.facebook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Docs: https://developers.facebook.com/docs/facebook-login/guides/advanced/manual-flow#exchangecode
@Serializable
data class FacebookOAuthTokenResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresInSeconds: Long,
)
