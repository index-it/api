package app.index_it.data.models.oauth.apple

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Docs: https://developer.apple.com/documentation/sign_in_with_apple/tokenresponse
@Serializable
data class AppleOAuthTokenResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String?,
    @SerialName("expires_in")
    val expiresInSeconds: Long,
    @SerialName("id_token")
    val idToken: AppleIdTokenDto
)
