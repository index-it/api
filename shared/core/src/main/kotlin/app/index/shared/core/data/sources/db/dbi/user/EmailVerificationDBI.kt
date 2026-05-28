package app.index.shared.core.data.sources.db.dbi.user

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.email.EmailVerificationData
import app.index.shared.core.data.models.user.UserData
import app.index.shared.core.data.sources.db.dbi.DBI

interface EmailVerificationDBI : DBI {
    suspend fun count(id: IxId<UserData>): Long

    suspend fun create(emailVerificationData: EmailVerificationData)

    suspend fun get(token: String): EmailVerificationData?

    suspend fun deleteAll(id: IxId<UserData>)

    suspend fun deleteExpired()
}
