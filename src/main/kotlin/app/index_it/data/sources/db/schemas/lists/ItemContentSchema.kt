package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.sources.db.schemas.lists.ItemContentTable.content
import app.index_it.data.sources.db.schemas.lists.ItemContentTable.id
import app.index_it.data.sources.db.schemas.lists.ItemContentTable.item
import app.index_it.data.sources.db.schemas.lists.ItemContentTable.user
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * @property id
 * @property user
 * @property item
 * @property content
 */
object ItemContentTable : UUIDTable() {
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val item = reference(
        name = "id_item",
        foreign = ItemTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val content = text("ix_content", eagerLoading = true)
}

/**
 * @property id
 * @property item
 * @property content
 * @property itemEntity
 */
class ItemContentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemContentEntity>(ItemContentTable)

    var user by ItemContentTable.user
    var item by ItemContentTable.item
    var content by ItemContentTable.content

    val userEntity by UserEntity referencedOn ItemContentTable.user
    val itemEntity by ItemEntity referencedOn ItemContentTable.item
}