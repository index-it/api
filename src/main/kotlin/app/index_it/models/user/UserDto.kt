package app.index_it.models.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class UserDto(
    @Contextual @SerialName("_id") val id: Id<UserDto> = ObjectId().toId(),
    val google_id: String? = null,
    val apple_id: String? = null,
    val email: String, // Received either via email registration, or google / apple oauth
    val password_hash: String? = null, // Null when the account gets created with an oauth provider (google, apple...)
    val email_verified: Boolean = false,
    val creation_timestamp: Long,
)
