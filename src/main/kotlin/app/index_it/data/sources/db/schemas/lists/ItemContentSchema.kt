package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.UUID

object ItemContentTable : UUIDTable() {
    val item = reference("item", ItemTable)
    val content = text("content")
}

class ItemContentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemContentEntity>(ItemContentTable)

    val item by ItemEntity referencedOn ItemContentTable.item
    val content by ItemContentTable.content
}