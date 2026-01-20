package app.index.data.sources.db.schemas.lists

import app.index.data.models.lists.ListUserInviteData
import app.index.data.sources.db.schemas.lists.ListUserInviteTable.created_at
import app.index.data.sources.db.schemas.lists.ListUserInviteTable.editor
import app.index.data.sources.db.schemas.lists.ListUserInviteTable.email
import app.index.data.sources.db.schemas.lists.ListUserInviteTable.expires_at
import app.index.data.sources.db.schemas.lists.ListUserInviteTable.list
import app.index.data.sources.db.schemas.lists.ListUserInviteTable.token
import app.index.data.sources.db.toEntityId
import app.index.data.sources.db.toIxId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

/**
 * @property id
 * @property token
 * @property email
 * @property list
 * @property editor
 * @property created_at
 * @property expires_at
 */
object ListUserInviteTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val email = varchar("email", 150).index()
    val list =
        reference(
            name = "id_list",
            foreign = ListTable,
            onDelete = ReferenceOption.CASCADE,
        )
    val editor = bool("editor")
    val created_at = timestamp("created_at")
    val expires_at = timestamp("expires_at")
}

/**
 * @property id
 * @property token
 * @property email
 * @property created_at
 * @property expires_at
 */
class ListUserInviteEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ListUserInviteEntity>(ListUserInviteTable)

    var token by ListUserInviteTable.token
    var email by ListUserInviteTable.email
    var list by ListUserInviteTable.list
    var editor by ListUserInviteTable.editor
    var created_at by ListUserInviteTable.created_at
    var expires_at by ListUserInviteTable.expires_at
}

fun ListUserInviteEntity.fromData(listUserInviteData: ListUserInviteData) {
    token = listUserInviteData.token
    email = listUserInviteData.email
    list = listUserInviteData.listId.toEntityId(ListTable)
    editor = listUserInviteData.editor
    created_at = Instant.ofEpochMilli(listUserInviteData.createdAt)
    expires_at = Instant.ofEpochMilli(listUserInviteData.expireAt)
}

fun ListUserInviteEntity.toData() =
    ListUserInviteData(
        token = token,
        email = email,
        listId = list.toIxId(),
        editor = editor,
        createdAt = created_at.toEpochMilli(),
        expireAt = expires_at.toEpochMilli(),
    )
