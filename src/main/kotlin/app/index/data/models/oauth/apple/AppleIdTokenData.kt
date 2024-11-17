package app.index.data.models.oauth.apple

data class AppleIdTokenData(
    val email: String,
    val emailVerified: Boolean,
    val isPrivateEmail: Boolean,
)
