package app.index_it.data.models.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.Validatable
import app.index_it.data.sources.db.schemas.lists.ItemContentEntity
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemContentDto(
    @Contextual @SerialName("_id") val id: IxId<ItemContentDto>,
    // @Contextual val userId: IxId<UserDto>,
    @Contextual val itemId: IxId<ItemDto>,
    val content: String,
) {
    @Serializable
    data class ItemContentCreateOrUpdateRequest(
        val content: String
    ): Validatable<ItemContentCreateOrUpdateRequest> {
        override fun validate() = Validation {
            ItemContentCreateOrUpdateRequest::content {
                minLength(1)
                maxLength(10000)
            }
        }.invoke(this)
    }
}

fun ItemContentEntity.fromDto(itemContentDto: ItemContentDto) {
    item = itemContentDto.itemId.toEntityId(ItemTable)
    content = itemContentDto.content
}

fun ItemContentEntity.toDto() = ItemContentDto(
    id = id.toIxId(),
    itemId = item.toIxId(),
    content = content
)
