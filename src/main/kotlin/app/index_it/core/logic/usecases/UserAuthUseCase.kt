package app.index_it.core.logic.usecases

import app.index_it.models.user.UserDto
import io.ktor.util.date.*
import kotlin.time.Duration.Companion.days

object UserAuthUseCase {
    /**
     * If an account didn't verify its email for more than 7 days it's considered non-existent
     */
    fun isIncompleteAccountOutdated(user: UserDto): Boolean {
        return !user.emailVerified && (getTimeMillis() - user.creationTimestamp) > 7.days.inWholeMilliseconds
    }
}
