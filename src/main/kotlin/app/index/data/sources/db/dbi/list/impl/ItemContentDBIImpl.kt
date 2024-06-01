package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ItemData
import app.index.data.sources.db.dbi.list.ItemContentDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemContentDBIImpl : ItemContentDBI {
    private fun itemFilter(itemId: IxId<ItemData>) = Op.build {
        ItemContentTable.item eq itemId.toEntityId(ItemTable)
    }

    override suspend fun create(itemContentData: ItemContentData) {
        dbQuery {
            ItemContentEntity.new(itemContentData.id.id) {
                fromData(itemContentData)
            }
        }
    }

    override suspend fun get(itemId: IxId<ItemData>): ItemContentData? =
        dbQuery {
            ItemContentEntity
                .find { itemFilter(itemId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun update(
        itemId: IxId<ItemData>,
        itemContentCreateOrUpdateRequestData: ItemContentData.ItemContentCreateOrUpdateRequestData,
    ): ItemContentData? =
        dbQuery {
            ItemContentTable.updateReturning(where = { itemFilter(itemId) }) {
                it[content] = itemContentCreateOrUpdateRequestData.content
            }.firstOrNull()?.let {
                ItemContentEntity.wrapRow(it).toData()
            }
        }

    override suspend fun delete(itemId: IxId<ItemData>) = dbQuery {
        ItemContentTable.deleteWhere { itemFilter(itemId) } > 0
    }
}
