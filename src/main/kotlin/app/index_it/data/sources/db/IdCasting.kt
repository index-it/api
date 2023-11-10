package app.index_it.data.sources.db

import app.index_it.core.logic.typedId.impl.IxId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

fun <T> EntityID<UUID>.toIxId(): IxId<T> = IxId(value)


fun IxId<*>.toEntityId(table: IdTable<UUID>) = EntityID(this.id, table)