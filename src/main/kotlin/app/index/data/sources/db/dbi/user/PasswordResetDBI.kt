package app.index.data.sources.db.dbi.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.PasswordResetDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.DBI

interface PasswordResetDBI : DBI {
    suspend fun count(id: IxId<UserDto>): Long

    suspend fun create(passwordResetDto: PasswordResetDto)

    suspend fun get(token: String): PasswordResetDto?

    suspend fun deleteAll(id: IxId<UserDto>)
}
