package app.index_it.data.sources.db.schemas.lists

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object ItemContentTable : UUIDTable() {
    val item = reference("item", ItemTable)
    val content = text("content", eagerLoading = true)
}

class ItemContentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemContentEntity>(ItemContentTable)

    val item by ItemEntity referencedOn ItemContentTable.item
    val content by ItemContentTable.content
}