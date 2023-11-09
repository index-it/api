package app.index_it.data.sources.db.schemas.lists

import app.index_it.core.logic.currentMillis
import app.index_it.data.models.Validatable
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.sources.db.schemas.tasks.TaskEntity
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.UUID

object ItemTable : UUIDTable() {
    val user = reference("user", UserTable).index()
    val list = reference("list", ListTable).index()
    val category = reference("category", CategoryTable)
    val task = reference("task", TaskTable)
    val name = varchar("name", 150)
    val completed = bool("completed")
    val createdAt = long("created_at")
    val editedAt = long("edited_at").nullable()
    val completedAt = long("completed_at").nullable()
}

class ItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemEntity>(ItemTable)

    val user by UserEntity referencedOn ItemTable.user
    val list by ListEntity referencedOn ItemTable.list
    val category by CategoryEntity referencedOn ItemTable.category
    val task by TaskEntity referencedOn ItemTable.task
    val name by ItemTable.name
    val completed by ItemTable.completed
    val createdAt by ItemTable.createdAt
    val editedAt by ItemTable.editedAt
    val completedAt by ItemTable.completedAt
}