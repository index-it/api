package app.index_it.daos.templates

import app.index_it.Env
import app.index_it.core.cache.TemplateListColorsCM
import app.index_it.core.cache.TemplateListNamesCM
import app.index_it.core.db.TemplateListColorsDBM
import app.index_it.core.db.TemplateListNamesDBM
import app.index_it.models.templates.ListColorsDto
import app.index_it.models.templates.ListNamesDto
import mu.KotlinLogging
import org.litote.kmongo.Id
import org.litote.kmongo.toId

private val logger = KotlinLogging.logger {  }

object ListTemplateDao {
    private val templateListNamesId: Id<ListNamesDto> = Env.template_list_names_id.toId()
    private val templateListColorsId: Id<ListColorsDto> = Env.template_list_colors_id.toId()

    fun getRandomListName(): String {
        val names = getListNames() ?: return "Duck duck"

        return names.names.randomOrNull() ?: run {
            logger.warn { "Empty array of template list names in template with id $templateListNamesId" }
            "Quack"
        }
    }

    fun getRandomListColor(): String {
        val colors = getListColors() ?: return "#FF000000"

        return colors.colors.randomOrNull() ?: run {
            logger.warn { "Empty array of template list colors in template with id $templateListColorsId" }
            "#FFFFFFFF"
        }
    }

    private fun getListNames() : ListNamesDto? {
        var names = TemplateListNamesCM.get(templateListNamesId)

        if (names == null) {
            names = TemplateListNamesDBM.get(templateListNamesId)
                ?: return null

            logger.warn { "No template for list names found with id $templateListNamesId" }

            TemplateListNamesCM.cache(names)
        }

        return names
    }

    private fun getListColors() : ListColorsDto? {
        var colors = TemplateListColorsCM.get(templateListColorsId)

        if (colors == null) {
            colors = TemplateListColorsDBM.get(templateListColorsId)
                ?: return null

            logger.warn { "No template for list colors found with id $templateListColorsId" }

            TemplateListColorsCM.cache(colors)
        }

        return colors
    }
}
