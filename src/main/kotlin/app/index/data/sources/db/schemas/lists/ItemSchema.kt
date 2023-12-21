package app.index.data.sources.db.schemas.lists

import app.index.data.models.lists.ItemData
import app.index.data.sources.db.schemas.lists.ItemTable.category
import app.index.data.sources.db.schemas.lists.ItemTable.completed
import app.index.data.sources.db.schemas.lists.ItemTable.completedAt
import app.index.data.sources.db.schemas.lists.ItemTable.createdAt
import app.index.data.sources.db.schemas.lists.ItemTable.editedAt
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
import java.util.*

/**
 * @property id
 * @property list
 * @property category
 * @property task
 * @property name
 * @property completed
 * @property createdAt
 * @property editedAt
 * @property completedAt
 */
object ItemTable : UUIDTable() {
    val user =
        reference(
            name = "id_user",
            foreign = UsersTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val list =
        reference(
            name = "id_list",
            foreign = ListTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val category =
        reference(
            name = "id_category",
            foreign = CategoryTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val task =
        reference(
            name = "id_task",
            foreign = TaskTable,
            onDelete = ReferenceOption.SET_NULL,
        ).nullable()
    val name = varchar("ix_name", 150)
    val completed = bool("completed")
    val createdAt = long("created_at")
    val editedAt = long("edited_at").nullable()
    val completedAt = long("completed_at").nullable()
}

/**
 * @property id
 * @property list
 * @property category
 * @property task
 * @property name
 * @property completed
 * @property createdAt
 * @property editedAt
 * @property completedAt
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
    var createdAt by ItemTable.createdAt
    var editedAt by ItemTable.editedAt
    var completedAt by ItemTable.completedAt

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
    user = itemData.userId.toEntityId(UsersTable)
    list = itemData.listId.toEntityId(ListTable)
    category = itemData.categoryId.toEntityId(CategoryTable)
    task = itemData.taskId?.toEntityId(TaskTable)
    name = itemData.name
    completed = itemData.completed
    createdAt = itemData.createdAt
    editedAt = itemData.editedAt
    completedAt = itemData.completedAt
}

fun ItemEntity.toData() =
    ItemData(
        id = id.toIxId(),
        userId = user.toIxId(),
        listId = list.toIxId(),
        categoryId = category.toIxId(),
        taskId = task?.toIxId(),
        name = name,
        completed = completed,
        createdAt = createdAt,
        editedAt = editedAt,
        completedAt = completedAt,
    )
