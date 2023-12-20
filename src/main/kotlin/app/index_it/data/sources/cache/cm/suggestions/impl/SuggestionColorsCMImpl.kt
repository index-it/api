package app.index_it.data.sources.cache.cm.suggestions.impl

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.sources.cache.cm.suggestions.SuggestionColorsCM
import app.index_it.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [SuggestionColorsCM::class])
class SuggestionColorsCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper
) : SuggestionColorsCM,
    HashedCM(
    keyName = "suggestion_colors",
    redisClient,
    objectMapper
) {
    override fun cache(colorSuggestionsDto: ColorSuggestionsDto) = cache(colorSuggestionsDto.id.toString(), colorSuggestionsDto)

    override fun get(id: IxIntId<ColorSuggestionsDto>) : ColorSuggestionsDto? = get(id.toString())
}
