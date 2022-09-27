package app.index_it.models.lists

import app.index_it.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

/**
 * Represents an item in a list
 */
@Serializable
data class ItemDto(
    @SerialName("_id") val id: String = ObjectId().toHexString(),
    val user_id: String,
    val list_id: String,
    val category_id: String,
    val name: String,
)

@Serializable
data class ClientItemDto(
    val category_id: String,
    val name: String
): Validatable<ClientItemDto> {
    override fun validate() = Validation<ClientItemDto> {
        ClientItemDto::name {
            minLength(1)
            maxLength(30)
        }
    }.invoke(this)
}
