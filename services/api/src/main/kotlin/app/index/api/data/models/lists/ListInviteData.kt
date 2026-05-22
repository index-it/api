package app.index.api.data.models.lists

import app.index.api.core.logic.DatetimeUtils
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.validation.Validatable
import app.index.api.data.validation.Validations
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
        var max_usages: Int? = null,
        var expires_at: LocalDateTime? = null,
        val description: String? = null,
    ) : Validatable<ListInviteCreateRequestData> {
        override fun validate() =
            Validation {
                ListInviteCreateRequestData::max_usages ifPresent {
                    minimum(Validations.ListInvite.MINIMUM_USAGES)
                    maximum(Validations.ListInvite.MAXIMUM_USAGES)
                }
                ListInviteCreateRequestData::description ifPresent {
                    minLength(Validations.ListInvite.MIN_DESCRIPTION_LENGTH)
                    maxLength(Validations.ListInvite.MAX_DESCRIPTION_LENGTH)
                }
            }.invoke(this)
    }

    @Serializable
    data class ListInviteResponseData(
        @Contextual val id: IxId<ListInviteData>,
        @Contextual val listId: IxId<ListData>,
        val editor: Boolean,
        val maxUsages: Int?,
        val description: String?,
        @Contextual val expiresAt: LocalDateTime?,
        @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
    )

    fun asResponseData() = ListInviteResponseData(
        id = id,
        listId = listId,
        editor = editor,
        maxUsages = maxUsages,
        description = description,
        expiresAt = expiresAt,
        createdAt = createdAt
    )
}