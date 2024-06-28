package app.index.data.models.auth

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
data class WelcomeActionResponse(
    @field:Schema(required = true)
    val action: WelcomeAction,
)
