package app.index.data.daos.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.FCMRegistrationTokenData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.FCMRegistrationTokenDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class FCMRegistrationTokenDao(
    private val fcmRegistrationTokenDBI: FCMRegistrationTokenDBI,
) {
    suspend fun upsert(fcmRegistrationTokenData: FCMRegistrationTokenData) {
        if (fcmRegistrationTokenDBI.exists(fcmRegistrationTokenData.token)) {
            fcmRegistrationTokenDBI.update(fcmRegistrationTokenData)
        } else {
            fcmRegistrationTokenDBI.create(fcmRegistrationTokenData)
        }
    }

    suspend fun getOfUser(id: IxId<UserData>): List<FCMRegistrationTokenData> {
        return fcmRegistrationTokenDBI.getOfUser(id)
    }

    suspend fun delete(token: String) {
        fcmRegistrationTokenDBI.delete(token)
    }

    suspend fun deleteExpired() {
        fcmRegistrationTokenDBI.deleteExpired()
    }
}
