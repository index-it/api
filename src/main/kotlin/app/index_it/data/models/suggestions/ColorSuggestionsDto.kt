package app.index_it.data.models.suggestions

import app.index_it.core.logic.typedId.impl.IxIntId
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorSuggestionsDto(
    @Contextual @SerialName("_id") val id: IxIntId<ColorSuggestionsDto>,
    val description: String,
    val colors: List<String>
)
