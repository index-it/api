package app.index_it.data.models.suggestions

import app.index_it.core.logic.typedId.impl.IxIntId
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NameSuggestionsDto(
    @Contextual @SerialName("_id") val id: IxIntId<NameSuggestionsDto>,
    val description: String,
    val names: List<String>
)