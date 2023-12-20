package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.list.ItemContentDBI
import app.index_it.data.sources.db.schemas.lists.*
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemContentDBIImpl : ItemContentDBI {
    private fun userAndItemFilter(userId: IxId<UserDto>, itemId: IxId<ItemDto>) = Op.build { (ItemContentTable.item eq itemId.toEntityId(ItemTable)) and (ItemContentTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(itemContentDto: ItemContentDto) {
        dbQuery {
            ItemContentEntity.new(itemContentDto.id.id) {
                fromDto(itemContentDto)
            }
        }
    }


    override suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? = dbQuery {
        ItemContentEntity
            .find { userAndItemFilter(userId, itemId) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun update(
        userId: IxId<UserDto>,
        itemId: IxId<ItemDto>,
        itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest
    ): Boolean = dbQuery {
        ItemContentTable.update({ userAndItemFilter(userId, itemId) }) {
            it[content] = itemContentCreateOrUpdateRequest.content
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        dbQuery {
            ItemContentTable.deleteWhere { userAndItemFilter(userId, itemId) }
        }
    }
}