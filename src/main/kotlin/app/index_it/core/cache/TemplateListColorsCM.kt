package app.index_it.core.cache

import app.index_it.models.templates.ListColorsDto
import org.litote.kmongo.Id

object TemplateListColorsCM : HashedCM("template_list_colors") {
    fun cache(listColorsDto: ListColorsDto) = cache(listColorsDto.id.toString(), listColorsDto)

    fun get(id: Id<ListColorsDto>) : ListColorsDto? = get(id.toString())
}
