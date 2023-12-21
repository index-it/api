package app.index.data.models.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.Validatable
import app.index.data.models.user.UserDto
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ItemContentDto(
    @Contextual val id: IxId<ItemContentDto>,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val itemId: IxId<ItemDto>,
    val content: String,
) {
    @Serializable
    data class ItemContentCreateOrUpdateRequest(
        val content: String,
    ) : Validatable<ItemContentCreateOrUpdateRequest> {
        override fun validate() =
            Validation {
                ItemContentCreateOrUpdateRequest::content {
                    minLength(1)
                    maxLength(10000)
                }
            }.invoke(this)
    }
}
