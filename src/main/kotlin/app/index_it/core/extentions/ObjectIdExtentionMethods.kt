package app.index_it.core.extentions

import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

/**
 * Converts a String to a typed Id by composing an [ObjectId] with the String and then parsing that to a typed Id
 */
fun <T> String.toObjectId(): Id<T> = ObjectId(this).toId()
