package app.index.shared.core.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsData(
    val email: String,
    val password: String,
)
