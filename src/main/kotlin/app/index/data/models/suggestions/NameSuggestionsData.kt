package app.index.data.models.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NameSuggestionsData(
    @Contextual val id: IxIntId<NameSuggestionsData>,
    val description: String,
    val names: List<String>,
    val locale: String
)
