package app.index.api.data.sources.db.schemas.lists

import app.index.shared.core.data.models.lists.CategoryData
import app.index.api.data.sources.db.schemas.lists.CategoryTable.color
import app.index.api.data.sources.db.schemas.lists.CategoryTable.created_at
import app.index.api.data.sources.db.schemas.lists.CategoryTable.edited_at
import app.index.api.data.sources.db.schemas.lists.CategoryTable.list
import app.index.api.data.sources.db.schemas.lists.CategoryTable.name
import app.index.api.data.sources.db.schemas.user.UserEntity
import app.index.api.data.sources.db.schemas.user.UsersTable
import app.index.api.data.sources.db.toEntityId
import app.index.api.data.sources.db.toIxId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * @property id
 * @property list
 * @property name
 * @property color
 * @property created_at
 * @property edited_at
 */
object CategoryTable : UUIDTable() {
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val list = reference(
        name = "id_list",
        foreign = ListTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val name = varchar("ix_name", 50)
    val color = varchar("color", 9).nullable()
    val created_at = timestamp("created_at")
    val edited_at = timestamp("edited_at").nullable()
}

/**
 * @property id
 * @property list
 * @property name
 * @property color
 *
 * @property listEntity
 * @property userEntity
 */
class CategoryEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryEntity>(CategoryTable)

    var user by CategoryTable.user
    var list by CategoryTable.list
    var name by CategoryTable.name
    var color by CategoryTable.color
    var created_at by CategoryTable.created_at
    var edited_at by CategoryTable.edited_at

    @Suppress("MemberVisibilityCanBePrivate")
    val userEntity by UserEntity referencedOn CategoryTable.user
    @Suppress("MemberVisibilityCanBePrivate")
    var listEntity by ListEntity referencedOn CategoryTable.list
}

fun CategoryEntity.fromData(categoryData: CategoryData) {
    user = categoryData.user_id.toEntityId(UsersTable)
    list = categoryData.list_id.toEntityId(ListTable)
    name = categoryData.name
    color = categoryData.color
    created_at = Instant.ofEpochMilli(categoryData.created_at)
    edited_at = categoryData.edited_at?.let { Instant.ofEpochMilli(it) }
}

fun CategoryEntity.toData() =
    CategoryData(
        id = id.toIxId(),
        user_id = user.toIxId(),
        list_id = list.toIxId(),
        name = name,
        color = color,
        created_at = created_at.toEpochMilli(),
        edited_at = edited_at?.toEpochMilli(),
    )
