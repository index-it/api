package app.index_it.daos.suggestions

import app.index_it.Env
import app.index_it.core.cache.SuggestionColorsCM
import app.index_it.core.cache.SuggestionListNamesCM
import app.index_it.core.db.SuggestionColors
import app.index_it.core.db.SuggestionListNamesDBM
import app.index_it.core.extentions.toObjectId
import app.index_it.models.suggestions.ColorSuggestionsDto
import app.index_it.models.suggestions.ListNameSuggestionsDto
import mu.KotlinLogging
import org.litote.kmongo.Id

private val logger = KotlinLogging.logger {  }

object SuggestionsDao {
    private val listNameSuggestionsId: Id<ListNameSuggestionsDto> = Env.suggestion_list_names_id.toObjectId()
    private val colorSuggestionsId: Id<ColorSuggestionsDto> = Env.suggestions_colors_id.toObjectId()

    fun getRandomListName(): String {
        val names = getListNames() ?: return "Duck duck"

        return names.names.randomOrNull() ?: run {
            logger.warn { "Empty array of template list names in template with id $listNameSuggestionsId" }
            "Quack"
        }
    }

    fun getRandomColor(): String {
        val colors = getColors() ?: return "#000000"

        return colors.colors.randomOrNull() ?: run {
            logger.warn { "Empty array of template list colors in template with id $colorSuggestionsId" }
            "#FFFFFF"
        }
    }

    fun getListNames() : ListNameSuggestionsDto? {
        var names = SuggestionListNamesCM.get(listNameSuggestionsId)

        if (names == null) {
            names = SuggestionListNamesDBM.get(listNameSuggestionsId)
                ?: run {
                    logger.warn { "No template for list names found with id $listNameSuggestionsId" }
                    return null
                }

            SuggestionListNamesCM.cache(names)
        }

        return names
    }

    fun getColors() : ColorSuggestionsDto? {
        var colors = SuggestionColorsCM.get(colorSuggestionsId)

        if (colors == null) {
            colors = SuggestionColors.get(colorSuggestionsId)
                ?: run {
                    logger.warn { "No template for list colors found with id $colorSuggestionsId" }
                    return null
                }

            SuggestionColorsCM.cache(colors)
        }

        return colors
    }
}
