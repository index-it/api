package app.index_it.models.user

import app.index_it.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class UserDto(
    @Contextual @SerialName("_id") val id: Id<UserDto> = ObjectId().toId(),
    val email: String,
    val name: String,
    val password_hash: String
)

@Serializable
data class ClientUserDto(
    val name: String
): Validatable<ClientUserDto> {
    override fun validate() = Validation<ClientUserDto> {
        Validation<ClientUserDto> {
            ClientUserDto::name {
                minLength(1)
                maxLength(50)
            }
        }
    }.invoke(this)
}
