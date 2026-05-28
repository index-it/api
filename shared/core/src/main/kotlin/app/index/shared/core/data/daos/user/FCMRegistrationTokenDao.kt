package app.index.shared.core.data.daos.user

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.FCMRegistrationTokenData
import app.index.shared.core.data.models.user.UserData
import app.index.shared.core.data.sources.db.dbi.user.FCMRegistrationTokenDBI
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
