package app.index.data.sources.db.schemas.lists

import app.index.data.models.lists.ListInvitationData
import app.index.data.sources.db.schemas.lists.ListInvitationTable.created_at
import app.index.data.sources.db.schemas.lists.ListInvitationTable.editor
import app.index.data.sources.db.schemas.lists.ListInvitationTable.email
import app.index.data.sources.db.schemas.lists.ListInvitationTable.expires_at
import app.index.data.sources.db.schemas.lists.ListInvitationTable.id
import app.index.data.sources.db.schemas.lists.ListInvitationTable.list
import app.index.data.sources.db.schemas.lists.ListInvitationTable.token
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
object ListInvitationTable : IntIdTable() {
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
class ListInvitationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ListInvitationEntity>(ListInvitationTable)

    var token by ListInvitationTable.token
    var email by ListInvitationTable.email
    var list by ListInvitationTable.list
    var editor by ListInvitationTable.editor
    var created_at by ListInvitationTable.created_at
    var expires_at by ListInvitationTable.expires_at
}

fun ListInvitationEntity.fromData(listInvitationData: ListInvitationData) {
    token = listInvitationData.token
    email = listInvitationData.email
    created_at = Instant.ofEpochMilli(listInvitationData.createdAt)
    expires_at = Instant.ofEpochMilli(listInvitationData.expireAt)
}

fun ListInvitationEntity.toData() =
    ListInvitationData(
        token = token,
        email = email,
        listId = list.toIxId(),
        editor = editor,
        createdAt = created_at.toEpochMilli(),
        expireAt = expires_at.toEpochMilli(),
    )
