package app.index_it.data.daos.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.FCMRegistrationTokenDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.impl.FCMRegistrationTokenDBIImpl

object FCMRegistrationTokenDao {
    suspend fun createOrUpdate(fcmRegistrationTokenDto: FCMRegistrationTokenDto) {
        if (FCMRegistrationTokenDBIImpl.exists(fcmRegistrationTokenDto.token)) {
            FCMRegistrationTokenDBIImpl.update(fcmRegistrationTokenDto)
        } else {
            FCMRegistrationTokenDBIImpl.create(fcmRegistrationTokenDto)
        }
    }

    suspend fun getOfUser(id: IxId<UserDto>): List<FCMRegistrationTokenDto> {
        return FCMRegistrationTokenDBIImpl.getOfUser(id)
    }

    suspend fun delete(token: String) {
        FCMRegistrationTokenDBIImpl.delete(token)
    }
}