package app.index.api.data.sources.db.schemas.lists

import app.index.api.data.models.lists.ListInviteData
import app.index.api.data.sources.db.schemas.lists.ListInviteTable.created_at
import app.index.api.data.sources.db.schemas.lists.ListInviteTable.editor
import app.index.api.data.sources.db.schemas.lists.ListInviteTable.expires_at
import app.index.api.data.sources.db.schemas.lists.ListInviteTable.list
import app.index.api.data.sources.db.schemas.lists.ListInviteTable.max_usages
import app.index.api.data.sources.db.schemas.lists.ListInviteTable.token
import app.index.api.data.sources.db.toEntityId
import app.index.api.data.sources.db.toIxId
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * @property id
 * @property token
 * @property list
 * @property editor
 * @property max_usages
 * @property created_at
 * @property expires_at
 */
object ListInviteTable : UUIDTable() {
    val token = varchar("token", 100).uniqueIndex()
    val list =
        reference(
            name = "id_list",
            foreign = ListTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val editor = bool("editor")
    val max_usages = integer("max_usages").nullable()
    val description = varchar("description", 100).nullable()
    val expires_at = datetime("expires_at").nullable()
    val created_at = timestamp("created_at")
}

/**
 * @property id
 * @property token
 * @property max_usages
 * @property created_at
 * @property expires_at
 */
class ListInviteEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ListInviteEntity>(ListInviteTable)

    var token by ListInviteTable.token
    var list by ListInviteTable.list
    var editor by ListInviteTable.editor
    var max_usages by ListInviteTable.max_usages
    var description by ListInviteTable.description
    var created_at by ListInviteTable.created_at
    var expires_at by ListInviteTable.expires_at
}

fun ListInviteEntity.fromData(listInviteData: ListInviteData) {
    token = listInviteData.token
    list = listInviteData.listId.toEntityId(ListTable)
    editor = listInviteData.editor
    max_usages = listInviteData.maxUsages
    description = listInviteData.description
    expires_at = listInviteData.expiresAt?.toJavaLocalDateTime()
    created_at = Instant.ofEpochMilli(listInviteData.createdAt)
}

fun ListInviteEntity.toData() =
    ListInviteData(
        id = id.toIxId(),
        token = token,
        listId = list.toIxId(),
        editor = editor,
        maxUsages = max_usages,
        description = description,
        createdAt = created_at.toEpochMilli(),
        expiresAt = expires_at?.toKotlinLocalDateTime(),
    )
