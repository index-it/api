package app.index.data.models.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.validation.Validatable
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ItemContentData(
    @Contextual val id: IxId<ItemContentData>,
    @Contextual val user_id: IxId<UserData>,
    @Contextual val item_id: IxId<ItemData>,
    val content: String,
) {
    @Serializable
    data class ItemContentCreateOrUpdateRequestData(
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
