package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.sources.db.dbi.list.ItemDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemDBIImpl : ItemDBI {
    private fun itemFilter(
        itemId: IxId<ItemData>,
    ) = Op.build {
        (ItemTable.id eq itemId.toEntityId(ItemTable))
    }

    private fun itemsFilter(
        itemIds: List<IxId<ItemData>>,
    ) = Op.build {
        ItemTable.id inList itemIds.map { it.toEntityId(ItemTable) }
    }

    override suspend fun create(itemData: ItemData) {
        dbQuery {
            ItemEntity.new(itemData.id.id) {
                fromData(itemData)
            }
        }
    }

    override suspend fun get(
        itemId: IxId<ItemData>,
    ): ItemData? =
        dbQuery {
            ItemEntity
                .find { itemFilter(itemId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun exists(
        itemId: IxId<ItemData>,
    ): Boolean =
        dbQuery {
            get(itemId) != null
        }

    override suspend fun getOfList(
        listId: IxId<ListData>,
    ): List<ItemData> =
        dbQuery {
            ItemEntity
                .find { (ItemTable.list eq listId.toEntityId(ListTable)) }
                .map { it.toData() }
        }

    override suspend fun getUncompletedOfList(
        listId: IxId<ListData>,
        ): List<ItemData> =
        dbQuery {
            ItemEntity
                .find { (ItemTable.list eq listId.toEntityId(ListTable) and (ItemTable.completed eq false)) }
                .map { it.toData() }
        }

    override suspend fun getCompletedOfList(
        listId: IxId<ListData>,
        ): List<ItemData> =
        dbQuery {
            ItemEntity
                .find { (ItemTable.list eq listId.toEntityId(ListTable) and (ItemTable.completed eq true)) }
                .map { it.toData() }
        }

    override suspend fun setCompletion(
        itemId: IxId<ItemData>,
        completed: Boolean,
    ): ItemData? =
        dbQuery {
            ItemTable.updateReturning(where = { itemFilter(itemId) }) {
                it[this.completed] = completed
                it[this.completed_at] = if (completed) DatetimeUtils.currentJavaInstant() else null
            }.firstOrNull()?.let {
                ItemEntity.wrapRow(it).toData()
            }
        }

    override suspend fun setCompletion(
        itemIds: List<IxId<ItemData>>,
        completed: Boolean,
    ): List<ItemData> =
        dbQuery {
            ItemTable.updateReturning(where = { itemsFilter(itemIds) }) {
                it[this.completed] = completed
                it[this.completed_at] = if (completed) DatetimeUtils.currentJavaInstant() else null
            }.map {
                ItemEntity.wrapRow(it).toData()
            }
        }

    override suspend fun update(
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): ItemData? =
        dbQuery {
            ItemTable.updateReturning(where = { itemFilter(itemId) }) {
                it[name] = itemUpdateRequestData.name
                it[category] = itemUpdateRequestData.category_id?.toEntityId(CategoryTable)
                it[link] = itemUpdateRequestData.link
                it[note] = itemUpdateRequestData.note
                it[edited_at] = DatetimeUtils.currentJavaInstant()
            }.firstOrNull()?.let {
                ItemEntity.wrapRow(it).toData()
            }
        }

    override suspend fun update(
        items: List<ItemData.MultipleItemUpdateRequestData>
    ): List<ItemData> {
        val updatedItems = mutableListOf<ItemData>()

        dbQuery {
            items.forEach { item ->
                ItemTable.updateReturning(where = { itemFilter(item.id) }) {
                    it[category] = item.category_id?.toEntityId(CategoryTable)
                }
                    .firstOrNull()
                    ?.let { updatedItems.add(ItemEntity.wrapRow(it).toData()) }
            }

            // we could otherwise use batchUpsert but I wanna avoid risking inserting
//            return ItemTable.batchUpsert(items) { item ->
//                this[ItemTable.id] = item.id.toEntityId(ItemTable)
//                this[ItemTable.category] = item.category_id?.toEntityId(CategoryTable)
//            }.map { ItemEntity.wrapRow(it).toData() }
        }

        return updatedItems
    }


    override suspend fun delete(
        itemId: IxId<ItemData>,
    ) : Boolean = dbQuery {
        ItemTable.deleteWhere { itemFilter(itemId) } > 0
    }

    override suspend fun delete(
        itemIds: List<IxId<ItemData>>
    ) : Boolean = dbQuery {
        ItemTable.deleteWhere { itemsFilter(itemIds) } } > 0
    }
}
