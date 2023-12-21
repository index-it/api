package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ItemData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.ItemContentDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemContentDBIImpl : ItemContentDBI {
    private fun userAndItemFilter(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) = Op.build { (ItemContentTable.item eq itemId.toEntityId(ItemTable)) and (ItemContentTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(itemContentData: ItemContentData) {
        dbQuery {
            ItemContentEntity.new(itemContentData.id.id) {
                fromData(itemContentData)
            }
        }
    }

    override suspend fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemContentData? =
        dbQuery {
            ItemContentEntity
                .find { userAndItemFilter(userId, itemId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun update(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        itemContentCreateOrUpdateRequestData: ItemContentData.ItemContentCreateOrUpdateRequestData,
    ): Boolean =
        dbQuery {
            ItemContentTable.update({ userAndItemFilter(userId, itemId) }) {
                it[content] = itemContentCreateOrUpdateRequestData.content
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) {
        dbQuery {
            ItemContentTable.deleteWhere { userAndItemFilter(userId, itemId) }
        }
    }
}
