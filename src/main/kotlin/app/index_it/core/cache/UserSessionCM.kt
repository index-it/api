package app.index_it.core.cache

import app.index_it.models.auth.UserSessionDto

object UserSessionCM : HashedCM("sessions") {
    fun get(id: String) : UserSessionDto? = getValue(id.toString())

    fun create(userSessionDto: UserSessionDto) = cacheValue(userSessionDto.id.toString(), userSessionDto)

    fun delete(id: String) = uncacheValue(id.toString())
}
