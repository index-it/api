package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.suggestions.ColorSuggestionsDto
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object SuggestionColors {
    private val col = MongoClient.database.getCollection<ColorSuggestionsDto>("suggestion-colors")

    fun get(id: Id<ColorSuggestionsDto>): ColorSuggestionsDto? {
        return col.findOne(ColorSuggestionsDto::id eq id)
    }
}
