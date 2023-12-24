package app.index.data.sources.db.dbi.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.FCMRegistrationTokenData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface FCMRegistrationTokenDBI : DBI {
    suspend fun exists(token: String): Boolean

    suspend fun create(fcmRegistrationToken: FCMRegistrationTokenData)

    suspend fun get(token: String): FCMRegistrationTokenData?

    suspend fun getOfUser(id: IxId<UserData>): List<FCMRegistrationTokenData>

    suspend fun update(fcmRegistrationToken: FCMRegistrationTokenData)

    suspend fun delete(tokenToDelete: String)

    suspend fun deleteExpired()
}
