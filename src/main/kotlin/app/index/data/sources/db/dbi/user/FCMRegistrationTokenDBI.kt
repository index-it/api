package app.index.data.sources.db.dbi.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.FCMRegistrationTokenDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.DBI

interface FCMRegistrationTokenDBI : DBI {
    suspend fun exists(token: String): Boolean

    suspend fun create(fcmRegistrationToken: FCMRegistrationTokenDto)

    suspend fun get(token: String): FCMRegistrationTokenDto?

    suspend fun getOfUser(id: IxId<UserDto>): List<FCMRegistrationTokenDto>

    suspend fun update(fcmRegistrationToken: FCMRegistrationTokenDto)

    suspend fun delete(tokenToDelete: String)

    suspend fun deleteExpired()
}
