package app.index.data.daos.auth

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.auth.UserSessionCookie
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.users.UserSessionCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserSessionDao(
    private val userSessionCM: UserSessionCM,
) {
    fun get(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    ) = userSessionCM.get(userId, sessionId)

    fun create(
        userId: IxId<UserData>,
        device: String?,
        ip: String,
    ): UserSessionCookie {
        val userSessionCookie = UserSessionCookie(newIxId(), userId)

        upsert(
            UserAuthSessionData(
                id = userSessionCookie.session_id,
                userId = userId,
                iat = DatetimeUtils.currentMillis(),
                deviceName = device,
                ip = ip,
            ),
        )

        return userSessionCookie
    }

    private fun upsert(userAuthSessionData: UserAuthSessionData) = userSessionCM.cache(userAuthSessionData)

    fun delete(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    ) = userSessionCM.delete(userId, sessionId)

    fun deleteAllOfUser(userId: IxId<UserData>) = userSessionCM.deleteAllOfUser(userId)
}
