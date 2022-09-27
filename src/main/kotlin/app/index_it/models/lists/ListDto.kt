package app.index_it.models.lists

import app.index_it.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

/**
 * Represents a single list, which can contain categories to organize list items in it
 */
@Serializable
data class ListDto(
    @SerialName("_id") val id: String = ObjectId().toHexString(),
    var user_id: String,
    var name: String,
    val categories: MutableList<CategoryDto> = mutableListOf(),
    var icon: String,
    var color: String
)

@Serializable
data class ClientListDto(
    var name: String,
    var icon: String,
    var color: String
): Validatable<ClientListDto> {
    override fun validate() = Validation<ClientListDto> {
        ClientListDto::name {
            minLength(1)
            maxLength(50)
        }
    }.invoke(this)
}
