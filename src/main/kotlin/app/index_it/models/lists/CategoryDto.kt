package app.index_it.models.lists

import app.index_it.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

/**
 * Groups items in a list, for example, a list of movies to watch can have categories for the genre.
 * 'name' is the category ID
 */
@Serializable
data class CategoryDto(
    val id: String = ObjectId().toHexString(),
    var name: String,
    var color: String
)

@Serializable
data class ClientCategoryDto(
    val name: String,
    val color: String
): Validatable<ClientCategoryDto> {
    override fun validate() = Validation<ClientCategoryDto> {
        ClientCategoryDto::name {
            minLength(1)
            maxLength(30)
        }
    }.invoke(this)
}
