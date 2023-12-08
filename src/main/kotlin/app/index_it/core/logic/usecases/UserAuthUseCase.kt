package app.index_it.core.logic.usecases

import app.index_it.core.logic.DatetimeUtils
import app.index_it.data.models.user.UserDto
import kotlin.time.Duration.Companion.days

object UserAuthUseCase {
    /**
     * If an account didn't verify its email for more than 7 days it's considered non-existent
     */
    fun isIncompleteAccountOutdated(user: UserDto): Boolean {
        return !user.emailVerified && (DatetimeUtils.currentMillis() - user.creationTimestamp) > 7.days.inWholeMilliseconds
    }
}
