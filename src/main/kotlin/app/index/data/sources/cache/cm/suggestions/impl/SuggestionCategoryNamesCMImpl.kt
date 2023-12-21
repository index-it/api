package app.index.data.sources.cache.cm.suggestions.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsDto
import app.index.data.sources.cache.cm.suggestions.SuggestionCategoryNamesCM
import app.index.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [SuggestionCategoryNamesCM::class])
class SuggestionCategoryNamesCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : SuggestionCategoryNamesCM,
    HashedCM(
        keyName = "suggestion_category_names",
        redisClient,
        objectMapper,
    ) {
    override fun cache(nameSuggestionsDto: NameSuggestionsDto) = cache(nameSuggestionsDto.id.toString(), nameSuggestionsDto)

    override fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto? = get(id.toString())
}
