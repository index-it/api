package app.index.data.models.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.Validatable
import app.index.data.models.user.UserData
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ItemContentData(
    @Contextual val id: IxId<ItemContentData>,
    @Contextual val userId: IxId<UserData>,
    @Contextual val itemId: IxId<ItemData>,
    val content: String,
) {
    @Serializable
    data class ItemContentCreateOrUpdateRequestData(
        val content: String,
    ) : Validatable<ItemContentCreateOrUpdateRequestData> {
        override fun validate() =
            Validation {
                ItemContentCreateOrUpdateRequestData::content {
                    minLength(1)
                    maxLength(10000)
                }
            }.invoke(this)
    }
}
