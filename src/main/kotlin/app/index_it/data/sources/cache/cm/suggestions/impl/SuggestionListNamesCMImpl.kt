package app.index_it.data.sources.cache.cm.suggestions.impl

import app.index_it.core.clients.RedisClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.cache.cm.suggestions.SuggestionListNamesCM
import app.index_it.data.sources.cache.core.HashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [SuggestionListNamesCM::class])
class SuggestionListNamesCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper
): SuggestionListNamesCM,
    HashedCM(
    keyName = "suggestion_list_names",
    redisClient,
    objectMapper
) {
    override fun cache(nameSuggestionsDto: NameSuggestionsDto) = cache(nameSuggestionsDto.id.toString(), nameSuggestionsDto)

    override fun get(id: IxIntId<NameSuggestionsDto>) : NameSuggestionsDto? = get(id.toString())
}
