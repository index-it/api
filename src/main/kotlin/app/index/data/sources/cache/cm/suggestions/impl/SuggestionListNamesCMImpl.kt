package app.index.data.sources.cache.cm.suggestions.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsData
import app.index.data.sources.cache.cm.suggestions.SuggestionListNamesCM
import app.index.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [SuggestionListNamesCM::class])
class SuggestionListNamesCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : SuggestionListNamesCM,
    DoubleHashedCM(
        keyBase = "suggestion_list_names",
        redisClient,
        objectMapper,
    ) {
    override fun cache(nameSuggestionsData: NameSuggestionsData) =
        cache(nameSuggestionsData.id.toString(), nameSuggestionsData.locale, nameSuggestionsData)

    override fun get(id: IxIntId<NameSuggestionsData>, locale: String): NameSuggestionsData? =
        get(id.toString(), locale)
}
