package app.index.data.models.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.validation.Validatable
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ItemContentData(
    @field:Schema(required = true)
    @Contextual val id: IxId<ItemContentData>,
    @field:Schema(required = true)
    @Contextual val user_id: IxId<UserData>,
    @field:Schema(required = true)
    @Contextual val item_id: IxId<ItemData>,
    @field:Schema(required = true)
    val content: String,
) {
    @Serializable
    data class ItemContentCreateOrUpdateRequestData(
        @field:Schema(required = true)
        val content: String,
    ) : Validatable<ItemContentCreateOrUpdateRequestData> {
        override fun validate() =
            Validation {
                ItemContentCreateOrUpdateRequestData::content {
                    minLength(Validations.ItemContent.MIN_CONTENT_LENGTH)
                    maxLength(Validations.ItemContent.MAX_CONTENT_LENGTH)
                }
            }.invoke(this)
    }
}
