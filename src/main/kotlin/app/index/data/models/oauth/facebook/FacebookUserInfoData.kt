package app.index.data.models.oauth.facebook

import kotlinx.serialization.Serializable

@Serializable
data class FacebookUserInfoData(
    val email: String, // The email is verified, see https://stackoverflow.com/questions/14280535/is-it-possible-to-check-if-an-email-is-confirmed-on-facebook
)
