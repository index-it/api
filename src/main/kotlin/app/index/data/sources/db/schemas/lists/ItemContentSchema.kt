package app.index.data.sources.db.schemas.lists

import app.index.data.models.lists.ItemContentData
import app.index.data.sources.db.schemas.lists.ItemContentTable.content
import app.index.data.sources.db.schemas.lists.ItemContentTable.id
import app.index.data.sources.db.schemas.lists.ItemContentTable.item
import app.index.data.sources.db.schemas.lists.ItemContentTable.user
import app.index.data.sources.db.schemas.user.UserEntity
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import app.index.data.sources.db.toIxId
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
    val user =
        reference(
            name = "id_user",
            foreign = UsersTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val item =
        reference(
            name = "id_item",
            foreign = ItemTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val content = text("ix_content", eagerLoading = true)
}

/**
 * @property id
 * @property item
 * @property content
 *
 * @property userEntity
 * @property itemEntity
 */
class ItemContentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemContentEntity>(ItemContentTable)

    var user by ItemContentTable.user
    var item by ItemContentTable.item
    var content by ItemContentTable.content

    @Suppress("MemberVisibilityCanBePrivate")
    val userEntity by UserEntity referencedOn ItemContentTable.user
    @Suppress("MemberVisibilityCanBePrivate")
    val itemEntity by ItemEntity referencedOn ItemContentTable.item
}

fun ItemContentEntity.fromData(itemContentData: ItemContentData) {
    user = itemContentData.userId.toEntityId(UsersTable)
    item = itemContentData.itemId.toEntityId(ItemTable)
    content = itemContentData.content
}

fun ItemContentEntity.toData() =
    ItemContentData(
        id = id.toIxId(),
        userId = user.toIxId(),
        itemId = item.toIxId(),
        content = content,
    )
