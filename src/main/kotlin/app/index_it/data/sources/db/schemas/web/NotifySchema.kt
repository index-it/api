package app.index_it.data.sources.db.schemas.web

import app.index_it.data.sources.db.schemas.web.ReleaseNotifyTable.email
import app.index_it.data.sources.db.schemas.web.ReleaseNotifyTable.id
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @property id
 * @property email
 */
object ReleaseNotifyTable : IntIdTable() {
    val email = varchar("email", 150).uniqueIndex()
}

/**
 * @property id
 * @property email
 */
class NotifyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotifyEntity>(ReleaseNotifyTable)

    var email by ReleaseNotifyTable.email
}
