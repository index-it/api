package app.index.data.models.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ColorSuggestionsData(
    @Contextual val id: IxIntId<ColorSuggestionsData>,
    val description: String,
    val colors: List<String>,
)
