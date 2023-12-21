package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.list.ListDBI
import app.index.data.sources.db.schemas.lists.ListEntity
import app.index.data.sources.db.schemas.lists.ListTable
import app.index.data.sources.db.schemas.lists.fromDto
import app.index.data.sources.db.schemas.lists.toDto
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListDBIImpl : ListDBI {
    private fun userAndListFilter(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ) = Op.build { (ListTable.id eq listId.toEntityId(ListTable)) and (ListTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(listDto: ListDto) {
        dbQuery {
            ListEntity.new(listDto.id.id) {
                fromDto(listDto)
            }
        }
    }

    override suspend fun get(id: IxId<UserDto>): List<ListDto> =
        dbQuery {
            ListEntity
                .find { ListTable.user eq id.toEntityId(UsersTable) }
                .map { it.toDto() }
        }

    override suspend fun get(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): ListDto? =
        dbQuery {
            ListEntity
                .find { userAndListFilter(userId, listId) }
                .limit(1)
                .firstOrNull()
                ?.toDto()
        }

    override suspend fun update(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
        listUpdateRequestDto: ListDto.ListUpdateRequestDto,
    ): Boolean =
        dbQuery {
            ListTable.update({ userAndListFilter(userId, listId) }) {
                it[name] = listUpdateRequestDto.name
                it[emoji] = listUpdateRequestDto.icon.first()
                it[color] = listUpdateRequestDto.color
                it[editedAt] = DatetimeUtils.currentMillis()
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ) {
        dbQuery {
            ListTable.deleteWhere { userAndListFilter(userId, listId) }
        }
    }
}
