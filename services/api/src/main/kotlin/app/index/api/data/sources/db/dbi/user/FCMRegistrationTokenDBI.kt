package app.index.api.data.sources.db.dbi.user

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.FCMRegistrationTokenData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.db.dbi.DBI

interface FCMRegistrationTokenDBI : DBI {
    suspend fun exists(token: String): Boolean

    suspend fun create(fcmRegistrationToken: FCMRegistrationTokenData)

    suspend fun get(token: String): FCMRegistrationTokenData?

    suspend fun getOfUser(id: IxId<UserData>): List<FCMRegistrationTokenData>

    suspend fun update(fcmRegistrationToken: FCMRegistrationTokenData)

    suspend fun delete(tokenToDelete: String)

    suspend fun deleteExpired()
}
