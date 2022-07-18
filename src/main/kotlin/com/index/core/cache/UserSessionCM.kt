package com.index.core.cache

import com.index.models.SessionDto

object UserSessionCM : HashedCM("sessions") {
    fun getSession(id: String) : SessionDto? = getValue(id)
}
