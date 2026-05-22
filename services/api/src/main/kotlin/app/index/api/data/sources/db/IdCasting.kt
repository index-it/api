package app.index.api.data.sources.db

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.core.logic.typedId.impl.IxIntId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

fun <T> EntityID<UUID>.toIxId(): IxId<T> = IxId(value)

@Suppress("UNUSED")
fun <T> EntityID<Int>.toIxIntId(): IxIntId<T> = IxIntId(value)

fun IxId<*>.toEntityId(table: IdTable<UUID>) = EntityID(this.id, table)

@Suppress("UNUSED")
fun IxIntId<*>.toEntityIntId(table: IdTable<Int>) = EntityID(this.id, table)