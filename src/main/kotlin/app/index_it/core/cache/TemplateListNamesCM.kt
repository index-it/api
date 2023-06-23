package app.index_it.core.cache

import app.index_it.models.templates.ListNamesDto
import org.litote.kmongo.Id

object TemplateListNamesCM: HashedCM("template_list_names") {
    fun cache(listNamesDto: ListNamesDto) = cache(listNamesDto.id.toString(), listNamesDto)

    fun get(id: Id<ListNamesDto>) : ListNamesDto? = get(id.toString())
}
