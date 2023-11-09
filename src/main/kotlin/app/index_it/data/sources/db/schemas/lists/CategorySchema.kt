package app.index_it.data.sources.db.schemas.lists

import app.index_it.core.logic.RegexPatterns
import app.index_it.data.models.Validatable
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.UUID

object CategoryTable : UUIDTable() {
    val user = reference("user", UserTable).index()
    val list = reference("list", ListTable).index()
    val name = varchar("name", 50)
    val color = char("color", 7)
}

class CategoryEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryEntity>(CategoryTable)

    val user by UserEntity referencedOn CategoryTable.user
    val list by ListEntity referencedOn CategoryTable.list
    val name by CategoryTable.name
    val color by CategoryTable.color
}