package app.index.data.sources.cache.cm.suggestions.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsDto
import app.index.data.sources.cache.cm.suggestions.SuggestionItemNamesCM
import app.index.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [SuggestionItemNamesCM::class])
class SuggestionItemNamesCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : SuggestionItemNamesCM,
    HashedCM(
        keyName = "suggestion_item_names",
        redisClient,
        objectMapper,
    ) {
    override fun cache(nameSuggestionsDto: NameSuggestionsDto) = cache(nameSuggestionsDto.id.toString(), nameSuggestionsDto)

    override fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto? = get(id.toString())
}
