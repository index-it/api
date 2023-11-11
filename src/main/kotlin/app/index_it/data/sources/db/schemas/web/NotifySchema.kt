package app.index_it.data.sources.db.schemas.web

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @property id
 * @property email
 */
object NotifyTable : IntIdTable() {
    val email = varchar("email", 150).uniqueIndex()
}

/**
 * @property id
 * @property email
 */
class NotifyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotifyEntity>(NotifyTable)

    var email by NotifyTable.email
}
