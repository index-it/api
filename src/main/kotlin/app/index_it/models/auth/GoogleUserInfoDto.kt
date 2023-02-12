package app.index_it.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserInfoDto(
    val email: String,

)
