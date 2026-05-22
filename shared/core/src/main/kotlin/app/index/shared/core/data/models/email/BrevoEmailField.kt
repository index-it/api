package app.index.shared.core.data.models.email

import kotlinx.serialization.Serializable

@Serializable
data class BrevoEmailField(
    val email: String,
)