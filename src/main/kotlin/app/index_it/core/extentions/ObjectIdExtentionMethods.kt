package app.index_it.core.extentions

import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Suppress("UNCHECKED_CAST")
fun <T> String.toDtoId(): Id<T> = ObjectId(this).toId()
