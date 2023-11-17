package app.index_it.data.models.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WelcomeAction {
    @SerialName("login")
    LOGIN,
    @SerialName("register")
    REGISTER,
}
