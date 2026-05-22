package app.index.api.data.sources.db.dbi.user

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.email.EmailVerificationData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.db.dbi.DBI

interface EmailVerificationDBI : DBI {
    suspend fun count(id: IxId<UserData>): Long

    suspend fun create(emailVerificationData: EmailVerificationData)

    suspend fun get(token: String): EmailVerificationData?

    suspend fun deleteAll(id: IxId<UserData>)

    suspend fun deleteExpired()
}
