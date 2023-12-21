package app.index.data.models.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NameSuggestionsDto(
    @Contextual val id: IxIntId<NameSuggestionsDto>,
    val description: String,
    val names: List<String>,
)
