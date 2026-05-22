package app.index.api.data.models.email

import kotlinx.serialization.Serializable

@Serializable
data class BrevoEmailField(
    val email: String,
)