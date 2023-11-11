package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.list.ListDBI
import app.index_it.data.sources.db.schemas.lists.ListEntity
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object ListDBIImpl : ListDBI {
    private fun ListEntity.fromDto(listDto: ListDto) {
        user = listDto.userId.toEntityId(UserTable)
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

    override suspend fun create(listDto: ListDto) {
        dbQuery {
            ListEntity.new(listDto.id.id) {
                fromDto(listDto)
            }
        }
    }

    override suspend fun get(id: IxId<UserDto>): List<ListDto> = dbQuery {
        ListEntity
            .find { ListTable.user eq id.toEntityId(UserTable) }
            .map { it.toDto() }
    }

    override suspend fun get(id: IxId<ListDto>): ListDto? = dbQuery {
        ListEntity.findById(id.id)?.toDto()
    }

    override suspend fun update(id: IxId<ListDto>, listUpdateRequestDto: ListDto.ListUpdateRequestDto) {
        dbQuery {
            ListTable.update({ ListTable.id eq id.toEntityId(ListTable) }) {
                it[name] = listUpdateRequestDto.name
                it[emoji] = listUpdateRequestDto.icon.first()
                it[color] = listUpdateRequestDto.color
                it[editedAt] = currentMillis()
            }
        }
    }

    override suspend fun delete(id: IxId<ListDto>) {
        dbQuery {
            ListTable.deleteWhere { ListTable.id eq id.toEntityId(ListTable) }
        }
    }

}