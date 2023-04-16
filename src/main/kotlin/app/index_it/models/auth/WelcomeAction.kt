package app.index_it.models.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WelcomeAction {
    @SerialName("login")
    LOGIN,
    @SerialName("register")
    REGISTER,
    @SerialName("verifyEmail")
    VERIFY_EMAIL
}
