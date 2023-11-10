package app.index_it.data.sources.db.dbi.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface EmailVerificationDBI : DBI {
    suspend fun count(id: IxId<UserDto>): Int
    suspend fun save(emailVerificationDto: EmailVerificationDto)
    suspend fun get(token: String): EmailVerificationDto?
    suspend fun deleteAll(id: IxId<UserDto>)
}