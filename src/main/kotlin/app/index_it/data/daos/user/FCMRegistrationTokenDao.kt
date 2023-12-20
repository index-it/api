package app.index_it.data.daos.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.FCMRegistrationTokenDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.FCMRegistrationTokenDBI
import app.index_it.data.sources.db.dbi.user.impl.FCMRegistrationTokenDBIImpl
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class FCMRegistrationTokenDao(
    private val fcmRegistrationTokenDBI: FCMRegistrationTokenDBI
) {
    suspend fun createOrUpdate(fcmRegistrationTokenDto: FCMRegistrationTokenDto) {
        if (fcmRegistrationTokenDBI.exists(fcmRegistrationTokenDto.token)) {
            fcmRegistrationTokenDBI.update(fcmRegistrationTokenDto)
        } else {
            fcmRegistrationTokenDBI.create(fcmRegistrationTokenDto)
        }
    }

    suspend fun getOfUser(id: IxId<UserDto>): List<FCMRegistrationTokenDto> {
        return fcmRegistrationTokenDBI.getOfUser(id)
    }

    suspend fun delete(token: String) {
        fcmRegistrationTokenDBI.delete(token)
    }

    suspend fun deleteExpired() {
        fcmRegistrationTokenDBI.deleteExpired()
    }
}