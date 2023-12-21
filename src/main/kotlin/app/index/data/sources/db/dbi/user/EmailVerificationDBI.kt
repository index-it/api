package app.index.data.sources.db.dbi.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.email.EmailVerificationDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.DBI

interface EmailVerificationDBI : DBI {
    suspend fun count(id: IxId<UserDto>): Long

    suspend fun create(emailVerificationDto: EmailVerificationDto)

    suspend fun get(token: String): EmailVerificationDto?

    suspend fun deleteAll(id: IxId<UserDto>)
}
