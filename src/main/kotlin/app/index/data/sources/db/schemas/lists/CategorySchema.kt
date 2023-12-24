package app.index.data.sources.db.schemas.lists

import app.index.data.models.lists.CategoryData
import app.index.data.sources.db.schemas.lists.CategoryTable.color
import app.index.data.sources.db.schemas.lists.CategoryTable.id
import app.index.data.sources.db.schemas.lists.CategoryTable.list
import app.index.data.sources.db.schemas.lists.CategoryTable.name
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
 * @property name
 * @property color
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
    val color = varchar("color", 9)
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
}

fun CategoryEntity.toData() =
    CategoryData(
        id = id.toIxId(),
        user_id = user.toIxId(),
        list_id = list.toIxId(),
        name = name,
        color = color,
    )
