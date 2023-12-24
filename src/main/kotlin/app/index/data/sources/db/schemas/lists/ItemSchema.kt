package app.index.data.sources.db.schemas.lists

import app.index.data.models.lists.ItemData
import app.index.data.sources.db.schemas.lists.ItemTable.category
import app.index.data.sources.db.schemas.lists.ItemTable.completed
import app.index.data.sources.db.schemas.lists.ItemTable.completed_at
import app.index.data.sources.db.schemas.lists.ItemTable.created_at
import app.index.data.sources.db.schemas.lists.ItemTable.edited_at
import app.index.data.sources.db.schemas.lists.ItemTable.id
import app.index.data.sources.db.schemas.lists.ItemTable.list
import app.index.data.sources.db.schemas.lists.ItemTable.name
import app.index.data.sources.db.schemas.lists.ItemTable.task
import app.index.data.sources.db.schemas.tasks.TaskEntity
import app.index.data.sources.db.schemas.tasks.TaskTable
import app.index.data.sources.db.schemas.user.UserEntity
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import app.index.data.sources.db.toIxId
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
 * @property category
 * @property task
 * @property name
 * @property completed
 * @property created_at
 * @property edited_at
 * @property completed_at
 */
object ItemTable : UUIDTable() {
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
    val category = reference(
        name = "id_category",
        foreign = CategoryTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.SET_NULL,
    ).nullable()
    val name = varchar("ix_name", 150)
    val completed = bool("completed")
    val created_at = timestamp("created_at")
    val edited_at = timestamp("edited_at").nullable()
    val completed_at = timestamp("completed_at").nullable()
}

/**
 * @property id
 * @property list
 * @property category
 * @property task
 * @property name
 * @property completed
 * @property created_at
 * @property edited_at
 * @property completed_at
 *
 * @property userEntity
 * @property listEntity
 * @property categoryEntity
 * @property taskEntity
 */
class ItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemEntity>(ItemTable)

    var user by ItemTable.user
    var list by ItemTable.list
    var category by ItemTable.category
    var task by ItemTable.task
    var name by ItemTable.name
    var completed by ItemTable.completed
    var created_at by ItemTable.created_at
    var edited_at by ItemTable.edited_at
    var completed_at by ItemTable.completed_at

    @Suppress("MemberVisibilityCanBePrivate")
    val userEntity by UserEntity referencedOn ItemTable.user
    @Suppress("MemberVisibilityCanBePrivate")
    val listEntity by ListEntity referencedOn ItemTable.list
    @Suppress("MemberVisibilityCanBePrivate")
    val categoryEntity by CategoryEntity referencedOn ItemTable.category
    @Suppress("MemberVisibilityCanBePrivate")
    val taskEntity by TaskEntity optionalReferencedOn ItemTable.task
}

fun ItemEntity.fromData(itemData: ItemData) {
    user = itemData.user_id.toEntityId(UsersTable)
    list = itemData.list_id.toEntityId(ListTable)
    category = itemData.category_id.toEntityId(CategoryTable)
    task = itemData.task_id?.toEntityId(TaskTable)
    name = itemData.name
    completed = itemData.completed
    created_at = Instant.ofEpochMilli(itemData.created_at)
    edited_at = itemData.edited_at?.let { Instant.ofEpochMilli(it) }
    completed_at = itemData.completed_at?.let { Instant.ofEpochMilli(it) }
}

fun ItemEntity.toData() =
    ItemData(
        id = id.toIxId(),
        user_id = user.toIxId(),
        list_id = list.toIxId(),
        category_id = category.toIxId(),
        task_id = task?.toIxId(),
        name = name,
        completed = completed,
        created_at = created_at.toEpochMilli(),
        edited_at = edited_at?.toEpochMilli(),
        completed_at = completed_at?.toEpochMilli(),
    )
