package app.index.data.models.web

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
data class NotifyDto(
    @field:Schema(required = true)
    val email: String,
)
