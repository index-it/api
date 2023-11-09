package app.index_it.data.sources.mongo

import app.index_it.core.logic.typedId.impl.IxId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.util.UUID

fun <T> EntityID<UUID>.toId(): IxId<T> = IxId(value)


fun <T : Comparable<T>, IxId> cast(table: IdTable<T>, id: T) = EntityID(id, table)

fun IxId<*>.toEntityId(table: IdTable<UUID>) = EntityID(this.id, table)