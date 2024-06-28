package app.index.data.models.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NameSuggestionsData(
    @field:Schema(required = true)
    @Contextual val id: IxIntId<NameSuggestionsData>,
    @field:Schema(required = true)
    val description: String,
    @field:Schema(required = true)
    val names: List<String>,
    @field:Schema(required = true)
    val locale: String
)
