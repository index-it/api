package app.index_it.data.models.web

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object NotifyTable : IntIdTable() {
    val email = varchar("email", 150).uniqueIndex()
}

class NotifyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotifyEntity>(NotifyTable)

    val email by NotifyTable.email
}
