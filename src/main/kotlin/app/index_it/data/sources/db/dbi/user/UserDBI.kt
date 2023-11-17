package app.index_it.data.sources.db.dbi.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface UserDBI : DBI {
    suspend fun create(userDto: UserDto)
    suspend fun get(id: IxId<UserDto>): UserDto?
    suspend fun get(email: String): UserDto?
    suspend fun verifyEmail(id: IxId<UserDto>)
    suspend fun resetPassword(id: IxId<UserDto>, newPasswordHashed: String, verifyEmail: Boolean)
    suspend fun delete(id: IxId<UserDto>)
}