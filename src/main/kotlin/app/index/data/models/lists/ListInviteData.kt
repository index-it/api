package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.validation.Validatable
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.maximum
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.minimum
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @param token should be randomly generated and hashed
 */
@Serializable
data class ListInviteData(
    @Contextual val id: IxId<ListInviteData>,
    val token: String,
    @Contextual val listId: IxId<ListData>,
    val editor: Boolean,
    val maxUsages: Int?,
    val description: String?,
    @Contextual val expiresAt: LocalDateTime?,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
) {
    @Serializable
    data class ListInviteCreateRequestData(
        var editor: Boolean,
        var maxUsages: Int? = null,
        var expiresAt: LocalDateTime? = null,
        val description: String? = null,
    ) : Validatable<ListInviteCreateRequestData> {
        override fun validate() =
            Validation {
                ListInviteCreateRequestData::maxUsages ifPresent {
                    minimum(Validations.ListInvite.MINIMUM_USAGES)
                    maximum(Validations.ListInvite.MAXIMUM_USAGES)
                }
                ListInviteCreateRequestData::description ifPresent {
                    minLength(Validations.ListInvite.MIN_DESCRIPTION_LENGTH)
                    maxLength(Validations.ListInvite.MAX_DESCRIPTION_LENGTH)
                }
            }.invoke(this)
    }
}