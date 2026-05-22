package app.index.api.data.sources.cache.cm.users

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData

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

    fun deleteAllOfUser(userId: IxId<UserData>)
}
