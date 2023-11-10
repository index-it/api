package app.index_it.data.daos.suggestions

import app.index_it.Env
import app.index_it.core.extentions.toObjectId
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.cache.cm.suggestions.*
import app.index_it.data.sources.mongo.suggestions.*
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {  }

object SuggestionsDao {
    private val listNameSuggestionsId: IxId<NameSuggestionsDto> = Env.suggestion_list_names_id.toObjectId()
    private val categoryNameSuggestionsId: IxId<NameSuggestionsDto> = Env.suggestion_category_names_id.toObjectId()
    private val itemNameSuggestionsId: IxId<NameSuggestionsDto> = Env.suggestion_item_names_id.toObjectId()
    private val taskNameSuggestionsId: IxId<NameSuggestionsDto> = Env.suggestion_task_names_id.toObjectId()

    private val colorSuggestionsId: IxId<ColorSuggestionsDto> = Env.suggestions_colors_id.toObjectId()

    fun getRandomNameSuggestion(nameSuggestionsDto: NameSuggestionsDto?): String {
        return nameSuggestionsDto?.names?.randomOrNull() ?: run {
            logger.warn { "Empty array of suggested names in suggestion with id $listNameSuggestionsId" }
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

    fun getListNames() : NameSuggestionsDto? {
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

    fun getCategoryNames() : NameSuggestionsDto? {
        var names = SuggestionCategoryNamesCM.get(categoryNameSuggestionsId)

        if (names == null) {
            names = SuggestionCategoryNamesDBM.get(categoryNameSuggestionsId)
                ?: run {
                    logger.warn { "No template for category names found with id $categoryNameSuggestionsId" }
                    return null
                }

            SuggestionCategoryNamesCM.cache(names)
        }

        return names
    }

    fun getItemNames() : NameSuggestionsDto? {
        var names = SuggestionItemNamesCM.get(itemNameSuggestionsId)

        if (names == null) {
            names = SuggestionItemNamesDBM.get(itemNameSuggestionsId)
                ?: run {
                    logger.warn { "No template for item names found with id $itemNameSuggestionsId" }
                    return null
                }

            SuggestionItemNamesCM.cache(names)
        }

        return names
    }

    fun getTaskNames() : NameSuggestionsDto? {
        var names = SuggestionTaskNamesCM.get(taskNameSuggestionsId)

        if (names == null) {
            names = SuggestionTaskNamesDBM.get(taskNameSuggestionsId)
                    ?: run {
                        logger.warn { "No template for task names found with id $taskNameSuggestionsId" }
                        return null
                    }

            SuggestionTaskNamesCM.cache(names)
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
