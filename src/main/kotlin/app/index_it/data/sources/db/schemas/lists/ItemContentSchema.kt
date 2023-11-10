package app.index_it.data.sources.db.schemas.lists

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * @property id
 * @property item
 * @property content
 */
object ItemContentTable : UUIDTable() {
    val item = reference(
        name = "item",
        foreign = ItemTable,
        onDelete = ReferenceOption.CASCADE
    )
    val content = text("content", eagerLoading = true)
}

/**
 * @property id
 * @property item
 * @property content
 * @property itemEntity
 */
class ItemContentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemContentEntity>(ItemContentTable)

    var item by ItemContentTable.item
    var content by ItemContentTable.content

    var itemEntity by ItemEntity referencedOn ItemContentTable.item
}