package app.index.data.sources.cache.cm.users

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.user.UserData

interface UserSessionCM {
    fun get(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    ): UserAuthSessionData?

    fun cache(userAuthSessionData: UserAuthSessionData)

    fun delete(
        userId: IxId<UserData>,
        sessionId: IxId<UserAuthSessionData>,
    )

    fun deleteAll(userId: IxId<UserData>)
}
