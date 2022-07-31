package app.index_it.core.cache

import app.index_it.models.SessionDto

object UserSessionCM : HashedCM("sessions") {
    fun getSession(id: String) : SessionDto? = getValue(id)
}
