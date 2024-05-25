package app.index.data.sources.db.schemas.lists

import app.index.data.sources.db.schemas.user.UserEntity
import app.index.data.sources.db.schemas.user.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption


/**
 * @property id
 * @property list
 * @property user
 */
object ListEditorTable : IntIdTable() {
    val list = reference(
        name = "id_list",
        foreign = ListTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
}

/**
 * @property id
 * @property list
 * @property user
 *
 * @property listEntity
 */
class ListEditorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ListEditorEntity>(ListEditorTable)

    var list by ListEditorTable.list
    var user by ListEditorTable.user

    @Suppress("MemberVisibilityCanBePrivate")
    val listEntity by ListEntity referencedOn ListEditorTable.list
    val userEntity by UserEntity referencedOn ListEditorTable.user
}