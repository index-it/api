package app.index.data.models.user

import app.index.core.logic.typedId.impl.IxId
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @Contextual val id: IxId<UserData>,
    val email: String, // Received either via email registration, or google / apple oauth
    val passwordHash: String?, // Null when the account gets created with an oauth provider (google, apple...)
    val emailVerified: Boolean, // Always true when user created with oauth, otherwise needs to be verified
    val creationTimestamp: Long,
    val creationSource: CreationSource,
) {
    @Serializable
    enum class CreationSource {
        @SerialName("google")
        GOOGLE,

        @SerialName("apple")
        @Suppress("UNUSED")
        APPLE,

        @SerialName("facebook")
        @Suppress("UNUSED")
        FACEBOOK,

        @SerialName("none")
        NONE,
    }

    fun getResponseDto() = UserResponseDto(id, email, creationTimestamp, creationSource)

    @Serializable
    data class UserResponseDto(
        @field:Schema(required = true)
        @Contextual val id: IxId<UserData>,
        @field:Schema(required = true)
        val email: String,
        @field:Schema(required = true)
        val creation_timestamp: Long,
        @field:Schema(required = true)
        val creation_source: CreationSource,
    )

    @Serializable
    data class AdminUserCreateRequestData(
        @field:Schema(required = true)
        val email: String,
        @field:Schema(required = true)
        val password: String,
        @field:Schema(required = true)
        val email_verified: Boolean,
        @field:Schema(required = true)
        val creation_source: CreationSource
    )
}
