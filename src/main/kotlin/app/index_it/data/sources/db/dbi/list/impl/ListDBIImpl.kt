package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.list.ListDBI
import app.index_it.data.sources.db.schemas.lists.ListEntity
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object ListDBIImpl : ListDBI {
    private fun ListEntity.fromDto(listDto: ListDto) {
        user = listDto.userId.toEntityId(UsersTable)
        name = listDto.name
        emoji = listDto.icon.first()
        color = listDto.color
        createdAt = listDto.createdAt
        editedAt = listDto.editedAt
    }

    private fun ListEntity.toDto() = ListDto(
        id = id.toIxId(),
        userId = user.toIxId(),
        name = name,
        icon = emoji.toString(),
        color = color,
        createdAt = createdAt,
        editedAt = editedAt
    )

    private fun userAndListFilter(userId: IxId<UserDto>, listId: IxId<ListDto>) = Op.build { (ListTable.id eq listId.toEntityId(ListTable)) and (ListTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(listDto: ListDto) {
        dbQuery {
            ListEntity.new(listDto.id.id) {
                fromDto(listDto)
            }
        }
    }

    override suspend fun get(id: IxId<UserDto>): List<ListDto> = dbQuery {
        ListEntity
            .find { ListTable.user eq id.toEntityId(UsersTable) }
            .map { it.toDto() }
    }

    override suspend fun get(userId: IxId<UserDto>, listId: IxId<ListDto>): ListDto? = dbQuery {
        ListEntity
            .find { userAndListFilter(userId, listId) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, listUpdateRequestDto: ListDto.ListUpdateRequestDto): Boolean = dbQuery {
        ListTable.update({ userAndListFilter(userId, listId) }) {
            it[name] = listUpdateRequestDto.name
            it[emoji] = listUpdateRequestDto.icon.first()
            it[color] = listUpdateRequestDto.color
            it[editedAt] = currentMillis()
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        dbQuery {
            ListTable.deleteWhere { userAndListFilter(userId, listId) }
        }
    }

}