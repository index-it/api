package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.sources.db.schemas.lists.CategoryTable.color
import app.index_it.data.sources.db.schemas.lists.CategoryTable.id
import app.index_it.data.sources.db.schemas.lists.CategoryTable.list
import app.index_it.data.sources.db.schemas.lists.CategoryTable.name
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*


/**
 * @property id
 * @property list
 * @property name
 * @property color
 */
object CategoryTable : UUIDTable() {
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val list = reference(
        name = "id_list",
        foreign = ListTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val name = varchar("ix_name", 50)
    val color = varchar("color", 9)
}

/**
 * @property id
 * @property list
 * @property name
 * @property color
 *
 * @property listEntity
 */
class CategoryEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryEntity>(CategoryTable)

    var user by CategoryTable.user
    var list by CategoryTable.list
    var name by CategoryTable.name
    var color by CategoryTable.color

    val userEntity by UserEntity referencedOn CategoryTable.user
    var listEntity by ListEntity referencedOn CategoryTable.list
}

fun CategoryEntity.fromDto(categoryDto: CategoryDto) {
    user = categoryDto.userId.toEntityId(UsersTable)
    list = categoryDto.listId.toEntityId(ListTable)
    name = categoryDto.name
    color = categoryDto.color
}

fun CategoryEntity.toDto() = CategoryDto(
    id = id.toIxId(),
    userId = user.toIxId(),
    listId = list.toIxId(),
    name = name,
    color = color
)