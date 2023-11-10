package app.index_it.data.sources.db.dbi.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.PasswordResetDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface PasswordResetDBI : DBI {
    suspend fun count(id: IxId<UserDto>): Int
    suspend fun save(passwordResetDto: PasswordResetDto)
    suspend fun get(token: String): PasswordResetDto?
    suspend fun deleteAll(id: IxId<UserDto>)
}
