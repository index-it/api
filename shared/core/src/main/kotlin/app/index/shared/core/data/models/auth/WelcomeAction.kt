package app.index.shared.core.data.models.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WelcomeAction {
    @SerialName("login")
    LOGIN,

    @SerialName("register")
    REGISTER,
}
