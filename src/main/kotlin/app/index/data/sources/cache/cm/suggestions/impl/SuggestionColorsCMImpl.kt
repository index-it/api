package app.index.data.sources.cache.cm.suggestions.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.ColorSuggestionsDto
import app.index.data.sources.cache.cm.suggestions.SuggestionColorsCM
import app.index.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [SuggestionColorsCM::class])
class SuggestionColorsCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : SuggestionColorsCM,
    HashedCM(
        keyName = "suggestion_colors",
        redisClient,
        objectMapper,
    ) {
    override fun cache(colorSuggestionsDto: ColorSuggestionsDto) = cache(colorSuggestionsDto.id.toString(), colorSuggestionsDto)

    override fun get(id: IxIntId<ColorSuggestionsDto>): ColorSuggestionsDto? = get(id.toString())
}
