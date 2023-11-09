package app.index_it.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeActionResponse(
    val action: WelcomeAction
)
