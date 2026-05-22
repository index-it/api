package app.index.shared.core.data.models.user

import app.index.shared.core.typedId.impl.IxId
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
    val has_pro: Boolean,
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

    fun getResponseDto() = UserResponseDto(
        id = id,
        email = email,
        creation_timestamp = creationTimestamp,
        creation_source = creationSource,
        has_pro = has_pro
    )

    @Serializable
    data class UserResponseDto(
        @Contextual val id: IxId<UserData>,
        val email: String,
        val creation_timestamp: Long,
        val creation_source: CreationSource,
        val has_pro: Boolean,
    )

    @Serializable
    data class AdminUserCreateRequestData(
        val email: String,
        val password: String,
        val email_verified: Boolean,
        val creation_source: CreationSource,
    )
}
