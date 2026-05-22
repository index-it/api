package app.index.api.data.sources.db.schemas.lists

import app.index.api.data.models.lists.ListData
import app.index.api.data.sources.db.schemas.lists.ListTable.color
import app.index.api.data.sources.db.schemas.lists.ListTable.created_at
import app.index.api.data.sources.db.schemas.lists.ListTable.edited_at
import app.index.api.data.sources.db.schemas.lists.ListTable.emoji
import app.index.api.data.sources.db.schemas.lists.ListTable.name
import app.index.api.data.sources.db.schemas.lists.ListTable.user
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
 * @property user
 * @property name
 * @property emoji
 * @property color
 * @property created_at
 * @property edited_at
 */
object ListTable : UUIDTable() {
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val name = varchar("ix_name", 100)
    val emoji = varchar("emoji", 10)
    val color = varchar("color", 9)
    val archived = bool("archived")
    val public = bool("public")
    val created_at = timestamp("created_at")
    val edited_at = timestamp("edited_at").nullable()
}

/**
 * @property id
 * @property user
 * @property name
 * @property emoji
 * @property color
 * @property created_at
 * @property edited_at
 * @property userEntity
 */
class ListEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ListEntity>(ListTable)

    var user by ListTable.user
    var name by ListTable.name
    var emoji by ListTable.emoji
    var color by ListTable.color
    var archived by ListTable.archived
    var public by ListTable.public
    var created_at by ListTable.created_at
    var edited_at by ListTable.edited_at

    @Suppress("MemberVisibilityCanBePrivate")
    val viewers by ListViewerEntity referrersOn ListViewerTable.list
    val editors by ListEditorEntity referrersOn ListEditorTable.list
    val userEntity by UserEntity referencedOn ListTable.user
}

fun ListEntity.fromData(listData: ListData) {
    user = listData.user_id.toEntityId(UsersTable)
    name = listData.name
    emoji = listData.icon
    color = listData.color
    archived = listData.archived
    public = listData.public
    created_at = Instant.ofEpochMilli(listData.created_at)
    edited_at = listData.edited_at?.let { Instant.ofEpochMilli(it) }
}

fun ListEntity.toData() =
    ListData(
        id = id.toIxId(),
        user_id = user.toIxId(),
        name = name,
        icon = emoji,
        color = color,
        archived = archived,
        public = public,
        viewers = viewers.map { it.user.toIxId() },
        editors = editors.map { it.user.toIxId() },
        created_at = created_at.toEpochMilli(),
        edited_at = edited_at?.toEpochMilli(),
    )

fun ListEntity.toListSingleUserAccessInfo() = viewers.map { viewer ->
    ListData.ListSingleUserAccessInfoResponseData(
        user_id = viewer.user.toIxId(),
        email = viewer.userEntity.email,
        editor = false
    )
} + editors.map { editor ->
    ListData.ListSingleUserAccessInfoResponseData(
        user_id = editor.user.toIxId(),
        email = editor.userEntity.email,
        editor = true
    )
}