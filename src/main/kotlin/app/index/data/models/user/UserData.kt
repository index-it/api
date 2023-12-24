package app.index.data.models.user

import app.index.core.logic.typedId.impl.IxId
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
        APPLE,

        @SerialName("facebook")
        FACEBOOK,

        @SerialName("none")
        NONE,
    }

    fun getResponseDto() = UserResponseDto(id, email, creationTimestamp, creationSource)

    @Serializable
    data class UserResponseDto(
        @Contextual val id: IxId<UserData>,
        val email: String,
        val creation_timestamp: Long,
        val creation_source: CreationSource,
    )
}
